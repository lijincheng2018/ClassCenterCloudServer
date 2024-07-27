package cn.ljcljc.task.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.exception.MultiObjectDeleteException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.common.MediaOutputObject;
import com.qcloud.cos.model.ciModel.job.FileCompressConfig;
import com.qcloud.cos.model.ciModel.job.FileProcessJobResponse;
import com.qcloud.cos.model.ciModel.job.FileProcessJobType;
import com.qcloud.cos.model.ciModel.job.FileProcessRequest;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ljc
 * @since 2024-07-19
 */

@Slf4j
public class COSUtils {
    private static COSClient initCos() {
        BasicCOSCredentials credentials = new BasicCOSCredentials(ConstantCosUtils.SECRET_ID, ConstantCosUtils.SECRET_KEY);
        Region region = new Region(ConstantCosUtils.REGION);
        ClientConfig clientConfig = new ClientConfig(region);

//        String serviceApiEndpoint = ConstantCosUtils.SERVICE_API_ENDPOINT;
//        String userEndpoint = ConstantCosUtils.URL;
//        UserSpecifiedEndpointBuilder endPointBuilder = new UserSpecifiedEndpointBuilder(userEndpoint, serviceApiEndpoint);
//        clientConfig.setEndpointBuilder(endPointBuilder);

        return new COSClient(credentials, clientConfig);
    }

    // 上传文件
    public static void uploadFile(MultipartFile file, String filePath) {
        COSClient cosClient = initCos();
        try {
            InputStream inputStream = file.getInputStream();
            // 使用BufferedInputStream包装，增加缓冲区
            InputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024 * 1024);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            // 上传文件
            cosClient.putObject(new PutObjectRequest(ConstantCosUtils.BUCKET_NAME, filePath, bufferedInputStream, metadata));
            cosClient.setBucketAcl(ConstantCosUtils.BUCKET_NAME, CannedAccessControlList.PublicRead);

        } catch (Exception e) {
            log.error("上传文件失败：" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    // 删除文件夹及其内容
    public static boolean deleteFolder(String folderPath) {
        COSClient cosClient = initCos();
        try {
            // 列出文件夹中的所有对象
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                    .withBucketName(ConstantCosUtils.BUCKET_NAME)
                    .withPrefix(folderPath);

            ObjectListing objectListing = cosClient.listObjects(listObjectsRequest);
            List<String> keys = new ArrayList<>();

            // 收集所有对象的Key
            while (objectListing != null) {
                objectListing.getObjectSummaries().forEach(s -> keys.add(s.getKey()));

                // 检查是否有更多对象
                if (objectListing.isTruncated()) {
                    objectListing = cosClient.listNextBatchOfObjects(objectListing);
                } else {
                    objectListing = null;
                }
            }

            if (!keys.isEmpty()) {
                // 删除所有对象
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(ConstantCosUtils.BUCKET_NAME)
                        .withKeys(keys.toArray(new String[0]));

                DeleteObjectsResult deleteObjectsResponse = cosClient.deleteObjects(deleteObjectsRequest);
                log.info("删除对象：" + deleteObjectsResponse.getDeletedObjects());
            }

            // 删除文件夹本身（如果它被视为对象）
            cosClient.deleteObject(ConstantCosUtils.BUCKET_NAME, folderPath);
            return true;

        } catch (MultiObjectDeleteException e) {
            log.error("删除对象失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("删除文件夹失败: " + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
        return false;
    }

    // 获取文件下载地址
    public static String getFileUrl(String key) {
        COSClient cosClient = initCos();
        try {

            Date expirationDate = new Date(System.currentTimeMillis() + ConstantCosUtils.EXPIRY_TIME);

            // 请求的 HTTP 方法，上传请求用 PUT，下载请求用 GET，删除请求用 DELETE
            HttpMethodName method = HttpMethodName.GET;

            URL url = cosClient.generatePresignedUrl(ConstantCosUtils.BUCKET_NAME, key, expirationDate, method);

            String urlStr = url.toString();
            urlStr = replaceDomain(urlStr, ConstantCosUtils.URL);

            return urlStr;

        } catch (Exception e) {
            log.error("获取文件下载地址失败：" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
        return null;
    }

    // 删除对象
    public static void deleteObject(String key) {
        COSClient cosClient = initCos();
        try {
            cosClient.deleteObject(ConstantCosUtils.BUCKET_NAME, key);
        } catch (Exception e) {
            log.error("删除对象失败：" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    public static String createFileCompressJob(String sourceDir, String outputFilePath) {
        COSClient cosClient = initCos();
        try {
            FileProcessRequest request = new FileProcessRequest();
            request.setBucketName(ConstantCosUtils.BUCKET_NAME);
            request.setTag(FileProcessJobType.FileCompress);
            FileCompressConfig fileCompressConfig = request.getOperation().getFileCompressConfig();
            fileCompressConfig.setFormat("zip");
            fileCompressConfig.setFlatten("1");
            fileCompressConfig.setPrefix(sourceDir);

            MediaOutputObject output = request.getOperation().getOutput();
            output.setBucket(ConstantCosUtils.BUCKET_NAME);
            output.setRegion(ConstantCosUtils.REGION);
            output.setObject("output/" + outputFilePath);

            FileProcessJobResponse response = cosClient.createFileProcessJob(request);

            return response.getJobDetail().getJobId();
        } catch (Exception e) {
            log.error("创建文件压缩任务失败：" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
        return null;
    }

    public static boolean waitForCompressJobCompletion(String jobId) {
        COSClient cosClient = initCos();
        try {
            while (true) {
                FileProcessRequest request = new FileProcessRequest();
                request.setBucketName(ConstantCosUtils.BUCKET_NAME);
                request.setJobId(jobId);

                FileProcessJobResponse response = cosClient.describeFileProcessJob(request);

                String state = response.getJobDetail().getState();
                if ("Success".equals(state)) {
                    return true;
                } else if ("Failed".equals(state)) {
                    return false;
                }
                Thread.sleep(1000); // 等待1秒钟后重试
            }
        } catch (Exception e) {
            log.error("等待文件压缩任务完成失败：" + e.getMessage());
        } finally {
            cosClient.shutdown();
        }
        return false;
    }

    public static String replaceDomain(String url, String newDomain) {
        // 使用正则表达式匹配URL中的域名部分
        return url.replaceAll("https://[^/]+", newDomain);
    }

}
