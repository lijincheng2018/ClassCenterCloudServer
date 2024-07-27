package cn.ljcljc.task.service.Impl;

import cn.ljcljc.api.client.MessageClient;
import cn.ljcljc.api.client.UserClient;
import cn.ljcljc.api.dto.UserDTO;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.task.domain.dto.RegisterDTO;
import cn.ljcljc.task.domain.entity.RegisterData;
import cn.ljcljc.task.domain.entity.RegisterList;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.task.domain.vo.UserCenterTaskFinishRateVO;
import cn.ljcljc.task.repository.RegisterDataRepository;
import cn.ljcljc.task.repository.RegisterListRepository;
import cn.ljcljc.task.service.RegisterService;
import cn.ljcljc.task.service.TaskTableService;
import cn.ljcljc.common.utils.ClassCenterUtils;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ljc
 * @since 2024-7-18
 */

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final RegisterListRepository registerListRepository;
    private final RegisterDataRepository registerDataRepository;
    private final TaskTableService taskTableService;
    private final UserClient userClient;
    private final MessageClient messageClient;

    /**
     * 获取全部登记表列表（超级管理员）
     *
     * @param bindClassId 绑定班级ID
     * @return 登记表列表
     */

    @Override
    public List<RegisterList> getRegisterList(String bindClassId) {
        return registerListRepository.findByBindClassOrderByIdDesc(bindClassId)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 获取全部登记表列表（普通管理员）
     *
     * @param bindClassId 绑定班级ID
     * @return 登记表列表
     */

    @Override
    public List<RegisterList> getRegisterListByClassId(String bindClassId, String classId) {
        return registerListRepository.findByBindClassAndClassIdOrderByIdDesc(bindClassId, classId)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
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
    public List<UserCenterTaskFinishRateVO> getRegisterFinishRate(String usergroup, String classid, String bindClass) {
        List<RegisterList> registerLists;
        if (Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(usergroup)) {
            registerLists = getRegisterList(bindClass);
        } else {
            registerLists = getRegisterListByClassId(bindClass, classid);
        }
        return registerLists.stream().map(registerList -> {
            Integer regId = registerList.getId();
            List<RegisterData> registerDataList = registerDataRepository.findByRegisterIdAndStatus(regId, "1");
            int countCntNum = registerDataList.size();
            float rate = ((float) countCntNum / (float) registerList.getPeopleNum()) * 100;
            String rateStr = String.format("%.2f%%", rate);
            return new UserCenterTaskFinishRateVO(registerList.getTitle(), rateStr, String.valueOf(rate));
        }).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public Result getRegisterDetail(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        Optional<RegisterList> registerOptional = registerListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (registerOptional.isEmpty()) {
            return Result.error("没有数据");
        }

        RegisterList register = registerOptional.get();

        boolean ifOutTime = "true".equals(register.getIfOutTime());
        boolean ifLimit = "true".equals(register.getIfLimit());

        int status = ClassCenterUtils.determineStatus(ifOutTime,
                ifLimit,
                register.getTimeFrame(),
                register.getStartTime(),
                register.getEndTime());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("id", register.getId());
        response.put("title", register.getTitle());
        response.put("if_finish", isRegisterFinished(currentUser.getClassId(), register.getId()));
        response.put("poster", register.getAuthor());
        response.put("status", status);

        return Result.success(response);
    }

    @Override
    @Transactional
    public Result getRegisterAdminList() {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        List<RegisterList> registers;
        if (Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            registers = registerListRepository.findByBindClassOrderByIdDesc(currentUser.getBindClass());
        } else {
            registers = registerListRepository.findByBindClassAndClassIdOrderByIdDesc(currentUser.getBindClass(), currentUser.getClassId());
        }

        List<Map<String, Object>> output = new ArrayList<>();
        for (RegisterList register : registers) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", register.getId());
            data.put("title", register.getTitle());
            data.put("time", register.getTime());
            data.put("jiezhi_time", register.getTimeFrame());
            data.put("poster", register.getAuthor());
            data.put("people_num", register.getPeopleNum());

            List<String> peopleList = Arrays.asList(register.getPeople().split(","));
            data.put("peoples", peopleList);

            int finishedPeople = registerDataRepository.countByRegisterIdAndStatus(register.getId(), "1");
            data.put("people_num_finish", finishedPeople);

            data.put("ifallow", register.getIsPublic().equals("1"));
            data.put("ifouttime", "true".equals(register.getIfOutTime()));
            data.put("iflimit", "true".equals(register.getIfLimit()));
            data.put("limit_time", Arrays.asList(register.getStartTime(), register.getEndTime()));

            output.add(data);
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

    @Override
    @Transactional
    public Result getRegisterInfo(Integer id) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup()))) {
            return Result.error("无权限");
        }

        Optional<RegisterList> registerOptional = registerListRepository.findById(id);
        if (registerOptional.isEmpty()) {
            return Result.error("没有数据");
        }

        RegisterList register = registerOptional.get();
        if (!currentUser.getClassId().equals(register.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        List<RegisterData> registerDataList = registerDataRepository.findByRegisterIdOrderByTimeAsc(register.getId());
        List<Map<String, Object>> output = new ArrayList<>();

        for (int i = 0; i < registerDataList.size(); i++) {
            RegisterData registerData = registerDataList.get(i);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("id", registerData.getUid());
            dataMap.put("name", registerData.getName());
            dataMap.put("classid", registerData.getClassId());
            dataMap.put("finish_time", registerData.getTime());
            dataMap.put("finish_rank", i + 1);
            output.add(dataMap);
        }

        boolean ifOutTime = "true".equals(register.getIfOutTime());
        boolean ifLimit = "true".equals(register.getIfLimit());
        String startTime = register.getStartTime();
        String endTime = register.getEndTime();

        int status = ClassCenterUtils.determineStatus(ifOutTime,
                ifLimit,
                register.getTimeFrame(),
                register.getStartTime(),
                register.getEndTime());


        if (!register.getIfLimit().equals("true")) {
            startTime = register.getTime();
            endTime = register.getTimeFrame().isEmpty() ? "无限制" : register.getTimeFrame();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("id", register.getId());
        response.put("title", register.getTitle());
        response.put("people_num", register.getPeopleNum());
        response.put("people_num_finish", registerDataRepository.countByRegisterIdAndStatus(register.getId(), "1"));
        response.put("list", output);
        response.put("is_public", register.getIsPublic().equals("1"));
        response.put("start_time", startTime);
        response.put("end_time", endTime);
        response.put("status", status);

        return Result.success(response);
    }

    @Override
    @Transactional
    public Result setRegister(Integer id) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        Optional<RegisterList> registerOptional = registerListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (registerOptional.isEmpty()) {
            return Result.error("没有数据");
        }

        RegisterList register = registerOptional.get();
        if (!isRegisterSettable(register)) {
            return Result.error("不在允许提交的时间范围内");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        RegisterData registerData = registerDataRepository.findByClassIdAndRegisterId(currentUser.getClassId(), id);
        if (registerData == null) {
            registerData = new RegisterData();
            registerData.setClassId(currentUser.getClassId());
            registerData.setRegisterId(id);
            registerData.setUid(currentUser.getUid());
            registerData.setName(currentUser.getName());
        }
        registerData.setStatus("1");
        registerData.setTime(formattedDateTime);
        registerDataRepository.save(registerData);

        return Result.success();
    }

    @Override
    @Transactional
    public Result setSingleRegister(Integer id, Integer pid, String mode) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup()))) {
            return Result.error("无权限");
        }

        Optional<RegisterList> registerOptional = registerListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (registerOptional.isEmpty()) {
            return Result.error("没有数据");
        }

        RegisterList register = registerOptional.get();
        if (!currentUser.getClassId().equals(register.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        RegisterData registerData = registerDataRepository.findByUidAndRegisterId(pid, id);
        if (registerData == null) {
            registerData = new RegisterData();
            registerData.setClassId(register.getClassId());
            registerData.setRegisterId(id);
            registerData.setUid(pid);
        }

        if ("finish".equals(mode)) {
            registerData.setStatus("1");
            registerData.setTime(formattedDateTime);
            registerDataRepository.save(registerData);
            return Result.success(Collections.singletonMap("finish_time", formattedDateTime));
        } else {
            registerData.setStatus("0");
            registerData.setTime("");
            registerDataRepository.save(registerData);
            return Result.success();
        }
    }

    @Override
    @Transactional
    public Result createRegister(RegisterDTO registerDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null || (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup()))) {
            return Result.error("没有权限");
        }

        if (registerDTO.getTitle() == null || registerDTO.getIfallow() == null || registerDTO.getPeople() == null) {
            return Result.error("参数错误");
        }

        if ("true".equals(registerDTO.getIfallow()) && "false".equals(registerDTO.getIflimit()) && registerDTO.getJiezhi_time() == null) {
            return Result.error("参数错误");
        }

        if ("true".equals(registerDTO.getIfallow()) && "true".equals(registerDTO.getIflimit()) && (registerDTO.getStart_time() == null || registerDTO.getEnd_time() == null)) {
            return Result.error("参数错误");
        }

        boolean isPublic = "true".equals(registerDTO.getIfallow());
        int isPublicInt = isPublic ? 1 : 0;

        List<String> peopleList = Arrays.asList(registerDTO.getPeople().split(","));
        int peopleNum = peopleList.size();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        RegisterList register = new RegisterList();
        register.setBindClass(currentUser.getBindClass());
        register.setTitle(registerDTO.getTitle());
        register.setPeople(registerDTO.getPeople());
        register.setPeopleNum(peopleNum);
        register.setAuthor(currentUser.getName());
        register.setClassId(currentUser.getClassId());
        register.setTime(formattedDateTime);
        register.setIsPublic(String.valueOf(isPublicInt));
        register.setTimeFrame(registerDTO.getJiezhi_time());
        register.setIfOutTime(registerDTO.getIfouttime());
        register.setIfLimit(registerDTO.getIflimit());
        register.setStartTime(registerDTO.getStart_time());
        register.setEndTime(registerDTO.getEnd_time());

        registerListRepository.save(register);

        if (isPublic) {
            String startOpenTime = "true".equals(registerDTO.getIflimit()) ? registerDTO.getStart_time() : formattedDateTime;
            String endOpenTime = "true".equals(registerDTO.getIflimit()) ? registerDTO.getEnd_time() : registerDTO.getJiezhi_time();

            String messageContent = String.format(
                    "登记表标题：%s<br>登记表ID：%d<br>开始提交的时间为：%s<br>结束提交的时间为：%s<br>请尽快完成任务~<br><a href=\"https://class.ljcljc.cn/user/finish_register/%d\">点击跳转到该任务</a>",
                    register.getTitle(), register.getId(), startOpenTime, endOpenTime, register.getId());
            messageClient.createMessage("【任务提醒】有新的登记表任务啦！", messageContent, registerDTO.getPeople());

            taskTableService.addTaskTable(register.getTitle(),
                    register.getId(),
                    Constant.USER_TASK_REGISTER_TYPE,
                    register.getTime());
        }

        for (String person : peopleList) {
            UserDTO user = userClient.getUser(Integer.parseInt(person), currentUser.getBindClass());

            RegisterData registerData = new RegisterData();
            registerData.setRegisterId(register.getId());
            registerData.setClassId(user.getClassId());
            registerData.setUid(user.getUid());
            registerData.setTime("");
            registerData.setName(user.getName());
            registerData.setStatus("0");
            registerDataRepository.save(registerData);
        }


        Map<String, Object> newData = new HashMap<>();
        newData.put("id", register.getId());
        newData.put("title", register.getTitle());
        newData.put("time", formattedDateTime);
        newData.put("jiezhi_time", registerDTO.getJiezhi_time());
        newData.put("poster", register.getAuthor());
        newData.put("peoples", peopleList);
        newData.put("people_num", peopleNum);
        newData.put("people_num_finish", 0);
        newData.put("ifallow", isPublic);

        return Result.success(newData);
    }

    @Override
    @Transactional
    public Result editRegister(Integer id, RegisterDTO registerDTO) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup()))) {
            return Result.error("没有权限");
        }

        if (registerDTO.getTitle() == null || registerDTO.getIfallow() == null) {
            return Result.error("参数错误");
        }

        if ("true".equals(registerDTO.getIfallow()) && "false".equals(registerDTO.getIflimit()) && registerDTO.getJiezhi_time() == null) {
            return Result.error("参数错误");
        }

        if ("true".equals(registerDTO.getIfallow()) && "true".equals(registerDTO.getIflimit()) && (registerDTO.getStart_time() == null || registerDTO.getEnd_time() == null)) {
            return Result.error("参数错误");
        }

        Optional<RegisterList> registerOptional = registerListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (registerOptional.isEmpty()) {
            return Result.error("没有数据");
        }

        RegisterList register = registerOptional.get();
        if (!currentUser.getClassId().equals(register.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        register.setTitle(registerDTO.getTitle());
        register.setIsPublic(String.valueOf("true".equals(registerDTO.getIfallow()) ? 1 : 0));
        register.setTimeFrame(registerDTO.getJiezhi_time());
        register.setIfOutTime(registerDTO.getIfouttime());
        register.setIfLimit(registerDTO.getIflimit());
        register.setStartTime(registerDTO.getStart_time());
        register.setEndTime(registerDTO.getEnd_time());

        registerListRepository.save(register);

        taskTableService.updateTaskTableName(register.getId(), register.getTitle());

        return Result.success();
    }

    @Override
    @Transactional
    public Result deleteRegister(Integer id) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_RP.equals(currentUser.getUserGroup()))) {
            return Result.error("无权限");
        }

        Optional<RegisterList> registerOptional = registerListRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (registerOptional.isEmpty()) {
            return Result.error("没有数据");
        }

        RegisterList register = registerOptional.get();
        if (!currentUser.getClassId().equals(register.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        registerListRepository.deleteById(id);
        registerDataRepository.deleteByRegisterId(id);
        taskTableService.deleteTaskTableByPid(register.getId());

        return Result.success();
    }

    private boolean isRegisterFinished(String classid, Integer registerid) {
        RegisterData registerData = registerDataRepository.findByClassIdAndRegisterId(classid, registerid);
        return registerData != null && "1".equals(registerData.getStatus());
    }

    private boolean isRegisterSettable(RegisterList register) {
        boolean ifOutTime = "true".equals(register.getIfOutTime());
        boolean ifLimit = "true".equals(register.getIfLimit());

        int status = ClassCenterUtils.determineStatus(ifOutTime,
                ifLimit,
                register.getTimeFrame(),
                register.getStartTime(),
                register.getEndTime());

        return status == 2;
    }
}
