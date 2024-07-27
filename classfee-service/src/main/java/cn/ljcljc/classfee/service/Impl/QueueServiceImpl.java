package cn.ljcljc.classfee.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.classfee.domain.dto.QueueDTO;
import cn.ljcljc.classfee.domain.entity.Queue;
import cn.ljcljc.classfee.domain.entity.SystemInfo;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.classfee.repository.QueueRepository;
import cn.ljcljc.classfee.repository.SystemInfoRepository;
import cn.ljcljc.classfee.service.QueueService;
import cn.ljcljc.common.utils.MD5Util;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author ljc
 * @since 2024-7-16
 */

@Service
public class QueueServiceImpl implements QueueService {
    private final QueueRepository queueRepository;
    private final SystemInfoRepository systemInfoRepository;


    @Autowired
    public QueueServiceImpl(QueueRepository queueRepository, SystemInfoRepository systemInfoRepository) {
        this.queueRepository = queueRepository;
        this.systemInfoRepository = systemInfoRepository;
    }

    @Override
    public List<Queue> getAllQueue(String bindClassId) {
        return queueRepository.findByBindClass(bindClassId).orElse(null);
    }

    @Override
    public List<Queue> getUnShenheQueue(String bindClassId) {
        return queueRepository.findByMethodAndBindClass("0", bindClassId).orElse(null);
    }

    @Override
    @Transactional
    public Result dealQueueItem(Integer id, QueueDTO queueDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        Queue queue = queueRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (queue == null) {
            return Result.error("该数据不存在");
        }

        if (queueDTO.getStatus() == null) {
            return Result.error("参数错误");
        }

        if ("1".equals(queueDTO.getStatus())) {
            SystemInfo systemInfo = systemInfoRepository.findByTagAndBindClass("classmoney", currentUser.getBindClass());
            Double classmoney = Double.parseDouble(systemInfo.getContent());

            classmoney -= Double.parseDouble(queue.getFee());
            systemInfo.setContent(String.valueOf(classmoney));
            systemInfoRepository.save(systemInfo);

            queueRepository.saveFee(queue.getBindClass(), queue.getTitle(), Double.valueOf(queue.getFee()), classmoney, queue.getPayment(), queue.getAuthor(), queue.getTime());
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        queue.setMethod(queueDTO.getStatus());
        queue.setPs(queueDTO.getPs());
        queue.setPfAuthor(currentUser.getName());
        queue.setPfTime(formattedDateTime);
        queueRepository.save(queue);

        return Result.success("success");
    }

    @Override
    @Transactional
    public Result getQueueDetail(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        Queue queue = queueRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (queue == null) {
            return Result.error("该数据不存在");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("img1", queue.getPhoto1());
        response.put("img2", queue.getPhoto2());
        response.put("yt", queue.getYt());
        response.put("ps", queue.getPs());
        response.put("dealer", queue.getPfAuthor());
        response.put("deal_time", queue.getPfTime());
        response.put("xiaofei_time", queue.getXiaofeiTime());

        return Result.success(response);
    }

    @Override
    public Result getQueueList() {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        List<Queue> queueList = queueRepository.findByBindClassOrderByIdDesc(currentUser.getBindClass());

        return Result.success(queueList);
    }

    @Override
    @Transactional
    public Result setNewQueueItem(String title, String money, String yt, String xiaofeiTime, MultipartFile file1, MultipartFile file2) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        if (title == null || money == null || yt == null || xiaofeiTime == null || file1.isEmpty() || file2.isEmpty()) {
            return Result.error("参数错误");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        // 获取项目根目录
        String rootDirectory = System.getProperty("user.dir");

        // 构建文件存储路径
        String uploadDir = rootDirectory + File.separator + "public" + File.separator + "file";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) {
                return Result.error("创建文件夹失败");
            }
        }

        String file1Name = generateFileName("1", Objects.requireNonNull(file1.getOriginalFilename()));
        String file2Name = generateFileName("2", Objects.requireNonNull(file2.getOriginalFilename()));

        try {
            file1.transferTo(new File(uploadDir + file1Name));
            file2.transferTo(new File(uploadDir + file2Name));
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }

        Queue queue = new Queue();
        queue.setBindClass(currentUser.getBindClass());
        queue.setTitle(title);
        queue.setYt(yt);
        queue.setFee(money);
        queue.setPayment("1");
        queue.setMethod("0");
        queue.setPhoto1(file1Name);
        queue.setPhoto2(file2Name);
        queue.setClassId(currentUser.getClassId());
        queue.setAuthor(currentUser.getName());
        queue.setPfAuthor("");
        queue.setPfTime("");
        queue.setTime(formattedDateTime);
        queue.setXiaofeiTime(xiaofeiTime);

        try {
            queueRepository.save(queue);

            Map<String, Object> newData = new HashMap<>();
            newData.put("id", queue.getId());
            newData.put("title", queue.getTitle());
            newData.put("money", queue.getFee());
            newData.put("method", queue.getPayment());
            newData.put("status", queue.getMethod());
            newData.put("poster", queue.getAuthor());
            newData.put("time", queue.getTime());

            return Result.success(newData);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private String generateFileName(String prefix, String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        String randNum = MD5Util.md5Hash(String.valueOf(Math.random())).substring(5, 11);
        return prefix + "-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + randNum + "." + extension;
    }
}
