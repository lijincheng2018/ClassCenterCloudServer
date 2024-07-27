package cn.ljcljc.task.utils;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantCosUtils implements InitializingBean {

    @Value("${tencent.cos.secretId}")
    public String secretId;

    @Value("${tencent.cos.secretKey}")
    public String secretKey;

    @Value("${tencent.cos.bucketName}")
    public String bucketName;

    @Value("${tencent.cos.region}")
    public String region;

    @Value("${tencent.cos.url}")
    public String url;

    public static String SECRET_ID;
    public static String SECRET_KEY;
    public static String BUCKET_NAME;
    public static String REGION;
    public static String URL;
    public static String SERVICE_API_ENDPOINT = "service.cos.myqcloud.com";
    public static Integer EXPIRY_TIME = 5 * 60 * 1000; // 过期时间 5分钟


    @Override
    public void afterPropertiesSet() {
        SECRET_ID = secretId;
        SECRET_KEY = secretKey;
        BUCKET_NAME = bucketName;
        REGION = region;
        URL = url;
    }
}