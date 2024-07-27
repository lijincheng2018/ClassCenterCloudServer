package cn.ljcljc.task.service.Impl;

import cn.ljcljc.api.client.MessageClient;
import cn.ljcljc.api.client.UserClient;
import cn.ljcljc.api.dto.UserDTO;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.ClassCenterUtils;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.MD5Util;
import cn.ljcljc.common.utils.UserContext;
import cn.ljcljc.task.domain.dto.CollectDTO;
import cn.ljcljc.task.domain.entity.CollectData;
import cn.ljcljc.task.domain.entity.CollectList;
import cn.ljcljc.task.domain.vo.CollectListVO;
import cn.ljcljc.task.domain.vo.UserCenterTaskFinishRateVO;
import cn.ljcljc.task.repository.CollectDataRepository;
import cn.ljcljc.task.repository.CollectListRepository;
import cn.ljcljc.task.service.CollectService;
import cn.ljcljc.task.service.TaskTableService;
import cn.ljcljc.task.utils.COSUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ljc
 * @since 2024-7-16
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CollectServiceImpl implements CollectService {

    private final CollectListRepository collectListRepository;
    private final CollectDataRepository collectDataRepository;
    private final TaskTableService taskTableService;
    private final UserClient userClient;
    private final MessageClient messageClient;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取全部收集表列表（超级管理员）
     *
     * @param bindClassId 绑定的班级ID
     * @return 收集表列表
     */

    @Override
    public List<CollectList> getCollectList(String bindClassId) {
        return collectListRepository.findByBindClassOrderByIdDesc(bindClassId).stream().limit(10).collect(Collectors.toList());
    }

    /**
     * 获取全部登记表列表（普通管理员）
     *
     * @param bindClassId 绑定的班级ID
     * @return 登记表列表
     */

    @Override
    public List<CollectList> getCollectListByClassId(String bindClassId, String classId) {
        return collectListRepository.findByBindClassAndClassIdOrderByIdDesc(bindClassId, classId).stream().limit(10).collect(Collectors.toList());
    }

    /**
     * 获取任务完成率
     *
     * @param usergroup 用户组
     * @param classid   学号
     * @param bindClass 绑定的班级ID
     * @return 任务完成率视图
     */

    @Override
    public List<UserCenterTaskFinishRateVO> getCollectFinishRate(String usergroup, String classid, String bindClass) {
        List<CollectList> collectLists;
        if (Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(usergroup)) {
            collectLists = getCollectList(bindClass);
        } else {
            collectLists = getCollectListByClassId(bindClass, classid);
        }
        return collectLists.stream().map(registerList -> {
            Integer regId = registerList.getId();
            List<CollectData> collectDataList = collectDataRepository.findByCollectIdAndStatus(regId, "1");
            int countCntNum = collectDataList.size();
            float rate = ((float) countCntNum / (float) registerList.getPeopleNum()) * 100;
            String rateStr = String.format("%.2f%%", rate);
            return new UserCenterTaskFinishRateVO(registerList.getTitle(), rateStr, String.valueOf(rate));
        }).collect(Collectors.toList());
    }

    @Override
    public Result getCollect(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (id == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> collectListOptional = collectListRepository.findById(id);
        if (collectListOptional.isEmpty() || !collectListOptional.get().getBindClass().equals(currentUser.getBindClass())) {
            return Result.error("没有数据");
        }

        CollectList collectList = collectListOptional.get();

        String title = collectList.getTitle();
        String poster = collectList.getAuthor();
        String sjNotice = Base64.getEncoder().encodeToString(collectList.getNotice().getBytes());
        String fileFormat = collectList.getFileFormat();

        String jiezhiTime = collectList.getTimeFrame();

        String ifRank = collectList.getIfRank();
        String ifShenhe = collectList.getIfShenhe();
        String isNeed = collectList.getIsNeed();

        CollectData collectData = collectDataRepository.findByClassIdAndCollectId(currentUser.getClassId(), id);
        if (collectData == null) {
            return Result.error("没有权限查看和提交此表");
        }


        boolean ifOutTime = "true".equals(collectList.getIfOutTime());
        boolean ifLimit = "true".equals(collectList.getIfLimit());

        int status = ClassCenterUtils.determineStatus(ifOutTime,
                ifLimit,
                collectList.getTimeFrame(),
                collectList.getStartTime(),
                collectList.getEndTime());

        String[] acceptFormats = fileFormat.split(",");
        StringBuilder formatListBuilder = new StringBuilder();
        for (String format : acceptFormats) {
            switch (format) {
                case "doc":
                    formatListBuilder.append(Constant.WORD_FILE_SUFFIX);
                    break;
                case "xls":
                    formatListBuilder.append(Constant.EXCEL_FILE_SUFFIX);
                    break;
                case "ppt":
                    formatListBuilder.append(Constant.POWERPOINT_FILE_SUFFIX);
                    break;
                case "pdf":
                    formatListBuilder.append(Constant.PDF_FILE_SUFFIX);
                    break;
                case "png":
                    formatListBuilder.append(Constant.PNG_FILE_SUFFIX);
                    break;
                case "zip":
                    formatListBuilder.append(Constant.ZIP_FILE_SUFFIX);
                    break;
            }
        }
        String formatList = formatListBuilder.substring(0, formatListBuilder.length() - 1);

        List<CollectData> collectDataList = collectDataRepository.findByCollectIdOrderByTimeAsc(id);

        Map<String, Object> response = new HashMap<>();
        boolean ifFinish = collectDataList.stream().anyMatch(data -> "1".equals(data.getStatus()));
        response.put("code", 200);
        response.put("id", id);
        response.put("title", title);
        response.put("if_finish", ifFinish);
        response.put("ifshenhe", "true".equals(ifShenhe));
        response.put("isneed", isNeed);
        response.put("poster", poster);
        response.put("status", status);
        response.put("jiezhi_time", jiezhiTime);
        response.put("sj_notice", sjNotice);
        response.put("ext", formatList);

        if ("true".equals(ifRank)) {
            List<Map<String, Object>> rankList = new ArrayList<>();
            collectDataList.stream().filter(data -> "1".equals(data.getStatus())).sorted(Comparator.comparing(CollectData::getTime)).limit(10).forEach(data -> {
                Map<String, Object> rank = new HashMap<>();
                rank.put("rank", rankList.size() + 1);
                rank.put("name", data.getName());
                rankList.add(rank);
            });
            response.put("rank", rankList);
        } else {
            response.put("rank", false);
        }

        if ("true".equals(ifShenhe)) {
            String shenhe = collectDataList.stream().map(CollectData::getShenhe).findFirst().orElse("");
            response.put("shenhe", shenhe);
        }

        return Result.success(response);
    }

    @Override
    @Transactional
    public Result uploadCollect(MultipartFile file, String mode, String uClassId, Integer id) {
        UserInfo currentUser = UserContext.getUser();


        if ("admin".equals(mode) && Constant.PERMISSION_GROUP_USER.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }
        if ("admin".equals(mode) && (uClassId == null || uClassId.isEmpty())) {
            return Result.error("参数不完整");
        }
        if (id == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> optionalCollectList = collectListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (optionalCollectList.isEmpty()) {
            return Result.error("没有数据");
        }

        CollectList collectList = optionalCollectList.get();
        if ("admin".equals(mode)) {
            if (!collectList.getClassId().equals(currentUser.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
                return Result.error("无权限");
            }
        }

        String colDatalist = collectList.getBond();
        String fileFormat = collectList.getFileFormat();
        String fileName = collectList.getFileRename();
        String ifrename = collectList.getIfRename();
        String ifFolder = collectList.getIfFolder();
        String folderName = collectList.getFolderName();

        String[] acceptFormats_check = fileFormat.split(",");
        StringBuilder formatListBuilder = new StringBuilder();
        for (String format : acceptFormats_check) {
            switch (format) {
                case "doc":
                    formatListBuilder.append(Constant.WORD_FILE_SUFFIX);
                    break;
                case "xls":
                    formatListBuilder.append(Constant.EXCEL_FILE_SUFFIX);
                    break;
                case "ppt":
                    formatListBuilder.append(Constant.POWERPOINT_FILE_SUFFIX);
                    break;
                case "pdf":
                    formatListBuilder.append(Constant.PDF_FILE_SUFFIX);
                    break;
                case "png":
                    formatListBuilder.append(Constant.PNG_FILE_SUFFIX);
                    break;
                case "zip":
                    formatListBuilder.append(Constant.ZIP_FILE_SUFFIX);
                    break;
            }
        }
        String formatList = formatListBuilder.substring(0, formatListBuilder.length() - 1);

        boolean ifOutTime = "true".equals(collectList.getIfOutTime());
        boolean ifLimit = "true".equals(collectList.getIfLimit());

        int status = ClassCenterUtils.determineStatus(ifOutTime,
                ifLimit,
                collectList.getTimeFrame(),
                collectList.getStartTime(),
                collectList.getEndTime());

        if (status == 1 || status == 3) {
            return Result.error("不在允许提交的时间范围内");
        }

        CollectData collectData;
        if ("admin".equals(mode)) {
            collectData = collectDataRepository.findByClassIdAndCollectId(uClassId, id);
        } else {
            collectData = collectDataRepository.findByClassIdAndCollectId(currentUser.getClassId(), id);
        }

        if (collectData == null) {
            return Result.error("没有权限提交此表");
        }

        List<String> acceptFormats = Arrays.asList(formatList.split(","));
        String postCheckList = String.join(",", acceptFormats);

        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        String extension = Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.') + 1).toLowerCase();
        log.info("postCheckList=>" + postCheckList + "     上传后缀: " + extension);

        if (!postCheckList.contains(extension)) {
            return Result.error("后缀不符合要求");
        }

        String uploadFileName;
        String folderPath = determineFolderPath(ifFolder, folderName, colDatalist, mode, uClassId, currentUser.getClassId(), collectData);

        if ("yes".equals(ifrename)) {
            uploadFileName = generateFileName(fileName, collectData, mode, uClassId, currentUser.getClassId(), extension);
        } else {
            uploadFileName = file.getOriginalFilename();
        }

        try {
            if ("yes".equals(ifFolder)) {
                if (collectData.getUploadFile() != null && !collectData.getUploadFile().isEmpty()) {
                    COSUtils.deleteFolder(folderPath);
                }
            } else {
                if (collectData.getUploadFile() != null && !collectData.getUploadFile().isEmpty()) {
                    COSUtils.deleteObject(folderPath + collectData.getUploadFile());
                }
            }


            COSUtils.uploadFile(file, folderPath + uploadFileName);

            long nowTime = System.currentTimeMillis() / 1000;
            String nowTimeStr = dateFormat.format(new Date(nowTime * 1000));

            if ("admin".equals(mode)) {
                collectDataRepository.updateCollectDataForAdmin(uClassId, nowTime, uploadFileName, id);
                return Result.success(Map.of("time", nowTimeStr));
            } else {
                collectDataRepository.updateCollectDataForUser(currentUser.getUid(), nowTime, uploadFileName, id);

                List<Map<String, Object>> rankList = generateRankList(id, collectList.getIfRank());
                boolean shenhe = "true".equals(collectList.getIfShenhe());

                return Result.success(Map.of("rank", rankList, "shenhe", shenhe));
            }
        } catch (Exception e) {
            return Result.error("提交失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result downloadCollect(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (id == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> optionalCollectList = collectListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (optionalCollectList.isEmpty()) {
            return Result.error("没有数据");
        }

        CollectList collectList = optionalCollectList.get();
        String colDatalist = collectList.getBond();
        String folderName = collectList.getFolderName();
        String ifFolder = collectList.getIfFolder();

        CollectData collectData = collectDataRepository.findByClassIdAndCollectId(currentUser.getClassId(), id);
        if (collectData == null) {
            return Result.error("没有权限！");
        }

        String uploadFileName = collectData.getUploadFile();
        String cid = collectData.getUid().toString();
        String cname = collectData.getName();

        String filePath = constructFilePath(ifFolder, folderName, colDatalist, uploadFileName, cname, cid, currentUser.getClassId());
        try {
            String signedUrl = COSUtils.getFileUrl(filePath);
            return Result.success(signedUrl);
        } catch (Exception e) {
            return Result.error("获取下载地址失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result downloadSingleCollect(Integer id, Integer pid) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null || pid == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> optionalCollectList = collectListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (optionalCollectList.isEmpty()) {
            return Result.error("没有数据");
        }

        CollectList collectList = optionalCollectList.get();
        String colDatalist = collectList.getBond();
        String folderName = collectList.getFolderName();
        String ifFolder = collectList.getIfFolder();

        CollectData collectData = collectDataRepository.findByUidAndCollectId(pid, id);
        if (collectData == null) {
            return Result.error("没有数据！");
        }

        String uploadFileName = collectData.getUploadFile();
        String cid = collectData.getUid().toString();
        String cname = collectData.getName();
        String uClassId = collectData.getClassId();

        String filePath = constructFilePath(ifFolder, folderName, colDatalist, uploadFileName, cname, cid, uClassId);
        try {
            String signedUrl = COSUtils.getFileUrl(filePath);
            return Result.success(signedUrl);
        } catch (Exception e) {
            return Result.error("获取下载地址失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result clearCollect(Integer id, String cid) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null || cid == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> optionalCollectList = collectListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (optionalCollectList.isEmpty()) {
            return Result.error("没有数据");
        }

        CollectList collectList = optionalCollectList.get();
        String colDatalist = collectList.getBond();
        String folderName = collectList.getFolderName();
        String ifFolder = collectList.getIfFolder();

        CollectData collectData = collectDataRepository.findByClassIdAndCollectId(cid, id);
        if (collectData == null) {
            return Result.error("没有权限！");
        }


        String uploadFileName = collectData.getUploadFile();
        String ccname = collectData.getName();
        String ccid = collectData.getUid().toString();

        String filePath = constructFilePath(ifFolder, folderName, colDatalist, uploadFileName, ccname, ccid, cid);

        if ("yes".equals(ifFolder)) {
            COSUtils.deleteFolder(filePath);
        } else {
            COSUtils.deleteObject(filePath);
        }


        collectDataRepository.clearCollectData(cid, id);
        return Result.success();
    }

    @Override
    @Transactional
    public Result createZIP(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> optionalCollectList = collectListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (optionalCollectList.isEmpty()) {
            return Result.error("没有数据");
        }

        CollectList collectList = optionalCollectList.get();
        String colDatalist = collectList.getBond();
        String title = collectList.getTitle();

        String zipname = title + System.currentTimeMillis() + (int) (Math.random() * 9000 + 1000) + ".zip";
        String key = Base64.getEncoder().encodeToString(zipname.getBytes());

        String sourceDir = "public/collect_files/" + colDatalist + "/";

        try {
            String jobId = COSUtils.createFileCompressJob(sourceDir, zipname);
            boolean isSuccess = COSUtils.waitForCompressJobCompletion(jobId);
            if (isSuccess) {
                return Result.success(key);
            } else {
                return Result.error("压缩任务失败");
            }
        } catch (Exception e) {
            return Result.error("错误：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result downloadZIP(String key) {
        UserInfo currentUser = UserContext.getUser();


        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (key == null || key.isEmpty()) {
            return Result.error("参数错误");
        }

        String fileName = new String(Base64.getDecoder().decode(key.replace(" ", "+")));
        String filePath = "output/" + fileName;

        try {
            String signedUrl = COSUtils.getFileUrl(filePath);
            return Result.success(signedUrl);
        } catch (Exception e) {
            return Result.error("获取下载地址失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result handleShenhe(String mode, Integer id, String classid) {
        UserInfo currentUser = UserContext.getUser();


        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null || classid == null || mode == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> optionalCollectList = collectListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (optionalCollectList.isEmpty()) {
            return Result.error("没有数据");
        }

        CollectList collectList = optionalCollectList.get();
        if (!currentUser.getClassId().equals(collectList.getClassId()) && !"1".equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }
        if (!"true".equals(collectList.getIfShenhe())) {
            return Result.error("无权限");
        }

        if ("accept".equals(mode)) {
            collectDataRepository.updateShenheStatus(classid, id, Constant.USER_TASK_COLLECT_SHENHE_ACCEPT);
            return Result.success();
        } else {
            collectDataRepository.updateShenheStatus(classid, id, Constant.USER_TASK_COLLECT_SHENHE_REJECT);
            return Result.success();
        }
    }

    @Override
    public Result getCollectDetail(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null) {
            return Result.error("参数错误");
        }

        Optional<CollectList> collectListOptional = collectListRepository.findById(id);
        if (collectListOptional.isEmpty() || !collectListOptional.get().getBindClass().equals(currentUser.getBindClass())) {
            return Result.error("没有数据");
        }

        CollectList collectList = collectListOptional.get();

        String title = collectList.getTitle();
        int peopleNum = collectList.getPeopleNum();
        String startTime = collectList.getStartTime();
        String endTime = collectList.getEndTime();
        String fileFormat = collectList.getFileFormat();
        String ifShenhe = collectList.getIfShenhe();

        boolean ifOutTime = "true".equals(collectList.getIfOutTime());
        boolean ifLimit = "true".equals(collectList.getIfLimit());

        int status = ClassCenterUtils.determineStatus(ifOutTime,
                ifLimit,
                collectList.getTimeFrame(),
                collectList.getStartTime(),
                collectList.getEndTime());

        if (!"true".equals(collectList.getIfLimit())) {
            startTime = collectList.getTime();
            endTime = collectList.getTimeFrame().isEmpty() ? "无限制" : collectList.getTimeFrame();
        }

        String[] acceptFormats = fileFormat.split(",");
        StringBuilder formatListBuilder = new StringBuilder();
        for (String format : acceptFormats) {
            switch (format) {
                case "doc":
                    formatListBuilder.append(Constant.WORD_FILE_SUFFIX);
                    break;
                case "xls":
                    formatListBuilder.append(Constant.EXCEL_FILE_SUFFIX);
                    break;
                case "ppt":
                    formatListBuilder.append(Constant.POWERPOINT_FILE_SUFFIX);
                    break;
                case "pdf":
                    formatListBuilder.append(Constant.PDF_FILE_SUFFIX);
                    break;
                case "png":
                    formatListBuilder.append(Constant.PNG_FILE_SUFFIX);
                    break;
                case "zip":
                    formatListBuilder.append(Constant.ZIP_FILE_SUFFIX);
                    break;
            }
        }
        String formatList = formatListBuilder.substring(0, formatListBuilder.length() - 1);

        List<CollectData> collectDataList = collectDataRepository.findByCollectIdOrderByTimeAsc(id);

        long finishedCount = collectDataList.stream().filter(data -> "1".equals(data.getStatus())).count();

        List<Map<String, Object>> output = new ArrayList<>();
        if ("true".equals(ifShenhe)) {
            for (int i = 0; i < collectDataList.size(); i++) {
                CollectData data = collectDataList.get(i);
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("id", data.getUid());
                studentData.put("name", data.getName());
                studentData.put("classid", data.getClassId());
                studentData.put("finish_time", (data.getTime() != null && !data.getTime().isEmpty()) ? dateFormat.format(new Date(Long.parseLong(data.getTime()) * 1000)) : "");
                studentData.put("finish_rank", i + 1);
                studentData.put("shenhe", data.getShenhe());
                output.add(studentData);
            }
        } else {
            for (int i = 0; i < collectDataList.size(); i++) {
                CollectData data = collectDataList.get(i);
                Map<String, Object> studentData = new HashMap<>();
                studentData.put("id", data.getUid());
                studentData.put("name", data.getName());
                studentData.put("classid", data.getClassId());
                studentData.put("finish_time", (data.getTime() != null && !data.getTime().isEmpty()) ? dateFormat.format(new Date(Long.parseLong(data.getTime()) * 1000)) : "");
                studentData.put("finish_rank", i + 1);
                output.add(studentData);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("id", id);
        response.put("title", title);
        response.put("people_num", peopleNum);
        response.put("people_num_finish", (int) finishedCount);
        response.put("list", output);
        response.put("start_time", startTime);
        response.put("end_time", endTime);
        response.put("status", status);
        response.put("ifshenhe", "true".equals(ifShenhe));
        response.put("ext", formatList);

        return Result.success(response);
    }

    @Override
    @Transactional
    public Result createCollect(CollectDTO collectDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("没有权限");
        }

        if (collectDTO.getTitle().isEmpty() || collectDTO.getAccept_format().isEmpty() || collectDTO.getPeople().isEmpty() ||
                ("true".equals(collectDTO.getIfrename()) && collectDTO.getFile_name().isEmpty()) ||
                ("true".equals(collectDTO.getIf_folder()) && collectDTO.getFolder_name().isEmpty()) ||
                ("false".equals(collectDTO.getIfLimit()) && collectDTO.getTime_frame().isEmpty()) ||
                ("true".equals(collectDTO.getIfLimit()) && (collectDTO.getStart_time().isEmpty() || collectDTO.getEnd_time().isEmpty()))) {
            return Result.error("参数错误");
        }

        String isRename = "true".equals(collectDTO.getIfrename()) ? "yes" : "no";
        String isFolder = "true".equals(collectDTO.getIf_folder()) ? "yes" : "no";

        List<String> peoples = Arrays.asList(collectDTO.getPeople().split(","));
        int peopleNum = peoples.size();

        List<String> acceptFormats = Arrays.asList(collectDTO.getAccept_format().split(","));
        int acceptFormatNum = acceptFormats.size();

        String ljcid = generateUniqueId();
        String dataListName = "sj_" + ljcid;


        String currentTime = dateFormat.format(new Date());

        CollectList newCollectList = new CollectList();
        newCollectList.setBindClass(currentUser.getBindClass());
        newCollectList.setTitle(collectDTO.getTitle());
        newCollectList.setPeople(collectDTO.getPeople());
        newCollectList.setPeopleNum(peopleNum);
        newCollectList.setFileRename(collectDTO.getFile_name());
        newCollectList.setIfRename(isRename);
        newCollectList.setFolderName(collectDTO.getFolder_name());
        newCollectList.setIfFolder(isFolder);
        newCollectList.setIfRank(collectDTO.getIfrank());
        newCollectList.setFileFormat(collectDTO.getAccept_format());
        newCollectList.setFileFormatNum(acceptFormatNum);
        newCollectList.setAuthor(currentUser.getName());
        newCollectList.setClassId(currentUser.getClassId());
        newCollectList.setBond(dataListName);
        newCollectList.setTime(currentTime);
        newCollectList.setNotice(collectDTO.getSj_notice());
        newCollectList.setTimeFrame(collectDTO.getTime_frame());
        newCollectList.setIfOutTime(collectDTO.getIfouttime());
        newCollectList.setIfLimit(collectDTO.getIfLimit());
        newCollectList.setStartTime(collectDTO.getStart_time());
        newCollectList.setEndTime(collectDTO.getEnd_time());
        newCollectList.setIfShenhe(collectDTO.getIfshenhe());
        newCollectList.setIsNeed(collectDTO.getIsneed());

        collectListRepository.save(newCollectList);
        Integer newId = newCollectList.getId();

        String startOpenTime = "true".equals(collectDTO.getIfLimit()) ? collectDTO.getStart_time() : currentTime;
        String endOpenTime = "true".equals(collectDTO.getIfLimit()) ? collectDTO.getEnd_time() : collectDTO.getTime_frame();

        String messageContent = String.format("收集表标题：%s<br>收集表ID：%d<br>开始提交的时间为：%s<br>结束提交的时间为：%s<br>请尽快完成任务~<br><a href=\"https://class.ljcljc.cn/user/collect_file/%d\">点击跳转到该任务</a>",
                collectDTO.getTitle(), newId, startOpenTime, endOpenTime, newId);

        messageClient.createMessage("【任务提醒】有新的收集表任务啦！", messageContent, collectDTO.getPeople());

        taskTableService.addTaskTable(newCollectList.getTitle(), newCollectList.getId(), Constant.USER_TASK_COLLECT_TYPE, currentTime);

        for (String person : peoples) {
            UserDTO user = userClient.getUser(Integer.parseInt(person), currentUser.getBindClass());

            CollectData collectData = new CollectData();
            collectData.setCollectId(newCollectList.getId());
            collectData.setClassId(user.getClassId());
            collectData.setUid(user.getUid());
            collectData.setTime("");
            collectData.setName(user.getName());
            collectData.setStatus("0");
            collectData.setUploadFile("");
            collectData.setShenhe("0");
            collectDataRepository.save(collectData);
        }

        Map<String, Object> newData = new HashMap<>();
        newData.put("id", newId);
        newData.put("title", newCollectList.getTitle());
        newData.put("time", currentTime);
        newData.put("jiezhi_time", newCollectList.getTimeFrame());
        newData.put("poster", currentUser.getName());
        newData.put("peoples", peoples);
        newData.put("people_num", peopleNum);
        newData.put("people_num_finish", 0);

        return Result.success(newData);
    }

    @Override
    @Transactional
    public Result editCollect(Integer id, CollectDTO collectDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("没有权限");
        }

        String title = collectDTO.getTitle();
        String acceptFormat = collectDTO.getAccept_format();
        String sjNotice = collectDTO.getSj_notice();
        String timeFrame = collectDTO.getTime_frame();
        String ifOutTime = collectDTO.getIfouttime();
        String ifLimit = collectDTO.getIfLimit();
        String startTime = collectDTO.getStart_time();
        String endTime = collectDTO.getEnd_time();
        String ifRank = collectDTO.getIfrank();
        String ifShenhe = collectDTO.getIfshenhe();
        String isNeed = collectDTO.getIsneed();

        if (id == null || title.isEmpty() || acceptFormat.isEmpty() ||
                ("false".equals(ifLimit) && timeFrame.isEmpty()) ||
                ("true".equals(ifLimit) && (startTime.isEmpty() || endTime.isEmpty()))) {
            return Result.error("参数错误");
        }

        CollectList collectList = collectListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("收集表不存在"));

        if (!collectList.getBindClass().equals(currentUser.getBindClass()) || (!collectList.getClassId().equals(currentUser.getClassId()) && !"1".equals(currentUser.getUserGroup()))) {
            return Result.error("无权限");
        }

        List<String> acceptFormats = Arrays.asList(acceptFormat.split(","));
        int acceptFormatNum = acceptFormats.size();

        collectList.setTitle(title);
        collectList.setIfRank(ifRank);
        collectList.setFileFormat(acceptFormat);
        collectList.setFileFormatNum(acceptFormatNum);
        collectList.setNotice(sjNotice);
        collectList.setTimeFrame(timeFrame);
        collectList.setIfOutTime(ifOutTime);
        collectList.setIfLimit(ifLimit);
        collectList.setStartTime(startTime);
        collectList.setEndTime(endTime);
        collectList.setIfShenhe(ifShenhe);
        collectList.setIsNeed(isNeed);

        collectListRepository.save(collectList);

        taskTableService.updateTaskTableName(id, title);

        return Result.success();
    }

    @Override
    public Result deleteCollect(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        CollectList collectList = collectListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("收集表不存在"));

        if (!collectList.getBindClass().equals(currentUser.getBindClass()) || (!collectList.getClassId().equals(currentUser.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()))) {
            return Result.error("无权限");
        }


        String listName = collectList.getBond();
        String folder = "public/collect_files/" + listName;
        boolean deleteSuccess = COSUtils.deleteFolder(folder);

        if (deleteSuccess) {
            collectDataRepository.deleteByCollectId(id);
            collectListRepository.deleteById(id);
            taskTableService.deleteTaskTableByPid(id);

            return Result.success();
        } else {
            return Result.error("删除文件夹失败");
        }
    }

    @Override
    public Result getCollectAdminList() {
        UserInfo currentUser = UserContext.getUser();

        List<CollectList> collectLists;
        if (Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            collectLists = collectListRepository.findByBindClassOrderByIdDesc(currentUser.getBindClass());
        } else {
            collectLists = collectListRepository.findByBindClassAndClassIdOrderByIdDesc(currentUser.getBindClass(), currentUser.getClassId());
        }

        List<CollectListVO> output = new ArrayList<>();
        for (CollectList collectList : collectLists) {
            Integer id = collectList.getId();
            String title = collectList.getTitle();
            String time = collectList.getTime();
            String jiezhiTime = collectList.getTimeFrame();
            String poster = collectList.getAuthor();
            Integer peopleNum = collectList.getPeopleNum();
            String people = collectList.getPeople();
            boolean ifShenhe = "true".equals(collectList.getIfShenhe());
            boolean isNeed = "true".equals(collectList.getIsNeed());

            long countCntNum = collectDataRepository.countByStatusAndCollectId(Constant.USER_TASK_COLLECT_SHENHE_ACCEPT, id);

            List<String> peoples = List.of(people.split(","));

            CollectListVO collectListVO = new CollectListVO(id, title, time, jiezhiTime, ifShenhe, isNeed, poster, peoples, peopleNum, countCntNum);
            output.add(collectListVO);
        }

        List<UserDTO> users = userClient.getUserList(currentUser.getBindClass());
        List<Map<String, Object>> peopleList = new ArrayList<>();
        for (UserDTO user : users) {
            Map<String, Object> person = new HashMap<>();
            person.put("id", user.getUid());
            person.put("name", user.getName());
            peopleList.add(person);
        }


        Map<String, Object> response = new HashMap<>();
        response.put("people_list", peopleList);
        response.put("result", output);

        return Result.success(response);

    }

    private String generateUniqueId() {
        Random random = new Random();
        int s1 = random.nextInt(699) + 19;
        long s2 = System.currentTimeMillis();
        String s3 = "ljc";
        String s = s1 + String.valueOf(s2) + s3;
        return MD5Util.md5Hash(s).substring(12, 26);
    }

    private String determineFolderPath(String ifFolder, String folderName, String colDatalist, String mode, String uClassid, String classid, CollectData data) {
        String folderPath;
        if ("yes".equals(ifFolder)) {
            String searchName = data.getName();
            String searchId = data.getUid().toString();
            String searchClassId = mode.equals("admin") ? uClassid : classid;

            folderPath = "public/collect_files/" + colDatalist + "/" +
                    folderName.replace("{name}", searchName)
                            .replace("{id}", searchId)
                            .replace("{id-plus}", String.format("%02d", Integer.parseInt(searchId)))
                            .replace("{classid}", searchClassId) + "/";
        } else {
            folderPath = "public/collect_files/" + colDatalist + "/";
        }
        return folderPath;
    }

    private String constructFilePath(String ifFolder, String folderName, String colDatalist, String uploadFileName,
                                     String cname, String cid, String classid) {
        String folderPath;
        if ("yes".equals(ifFolder)) {
            folderPath = folderName.replace("{name}", cname)
                    .replace("{id}", cid)
                    .replace("{id-plus}", String.format("%02d", Integer.parseInt(cid)))
                    .replace("{classid}", classid);
            return "public/collect_files/" + colDatalist + "/" + folderPath + "/" + uploadFileName;
        } else {
            return "public/collect_files/" + colDatalist + "/" + uploadFileName;
        }
    }

    private String generateFileName(String fileNameTemplate, CollectData data, String mode, String uClassid, String classid, String extension) {
        String searchName = data.getName();
        String searchId = data.getUid().toString();
        String searchClassId = "admin".equals(mode) ? uClassid : classid;

        String fileName = fileNameTemplate.replace("{name}", searchName)
                .replace("{id}", searchId)
                .replace("{id-plus}", String.format("%02d", Integer.parseInt(searchId)))
                .replace("{classid}", searchClassId);

        return fileName + "." + extension;
    }

    private List<Map<String, Object>> generateRankList(Integer collectid, String ifRank) {
        if (!"true".equals(ifRank)) {
            return Collections.emptyList();
        }

        List<CollectData> rankDataList = collectDataRepository.findTop10ByCollectIdAndStatusOrderByTimeAsc(collectid, "1");
        List<Map<String, Object>> rankList = new ArrayList<>();

        for (int i = 0; i < rankDataList.size(); i++) {
            CollectData data = rankDataList.get(i);
            Map<String, Object> rank = new HashMap<>();
            rank.put("rank", i + 1);
            rank.put("name", data.getName());
            rankList.add(rank);
        }

        return rankList;
    }
}
