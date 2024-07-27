package cn.ljcljc.user.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.ljcljc.api.client.*;
import cn.ljcljc.api.dto.*;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.domain.dto.UserDTO;
import cn.ljcljc.user.domain.entity.AllUser;
import cn.ljcljc.user.domain.entity.User;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.user.domain.vo.AdminHomeDataVO;
import cn.ljcljc.user.domain.vo.UserDataVO;
import cn.ljcljc.user.domain.vo.UserHomeDataVO;
import cn.ljcljc.user.repository.AllUserRepository;
import cn.ljcljc.user.repository.UserRepository;
import cn.ljcljc.user.service.UserAuthService;
import cn.ljcljc.user.service.UserService;
import cn.ljcljc.common.utils.ClassCenterUtils;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cn.ljcljc.common.utils.MD5Util.md5Hash;

/**
 * @author ljc
 * @since 2024-7-16
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAuthService userAuthService;
    private final AllUserRepository allUserRepository;
    private final UserRepository userRepository;
    private final MemorandumClient memorandumClient;
    private final NoticeClient noticeClient;
    private final MessageClient messageClient;
    private final SayClient sayClient;
    private final QueueClient queueClient;
    private final TaskClient taskClient;


    /**
     * 获取用户首页数据
     */
    @Override
    public Result getUserHomeData() {
        UserInfo currentUser = UserContext.getUser();
        List<NoticeDTO> noticeList;
        List<MessagesDTO> UnReadMessagesList;
        List<MemorandumDTO> memorandumList;
        Boolean isInitPwd = userAuthService.checkInitialPwd();

        if (currentUser != null) {
            noticeList = noticeClient.getList(currentUser.getBindClass());
            UnReadMessagesList = messageClient.getUnreadMessageList(String.valueOf(currentUser.getUid()), currentUser.getBindClass());
            memorandumList = memorandumClient.getList(currentUser.getClassId());
        } else {
            return Result.error("用户不存在");
        }

        int UnReadMessagesCount = (UnReadMessagesList != null) ? UnReadMessagesList.size() : 0;

        User user = userRepository.findByClassId(currentUser.getClassId());

        return Result.success(new UserHomeDataVO(
                user.getUserGroup(),
                user.getZhiwu(),
                noticeList,
                user.getClazz(),
                UnReadMessagesCount,
                memorandumList,
                isInitPwd));
    }

    /**
     * 获取管理员首页数据
     */

    @Override
    public Result getAdminHomeData() {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        List<User> AllUser = getAllUser();
        List<User> TuanyuanUser = getTuanyuanUser();

        Integer AllUserCount = (AllUser != null) ? AllUser.size() : 0;
        Integer TuanyuanUserCount = (TuanyuanUser != null) ? TuanyuanUser.size() : 0;

        List<QueueDTO> queueList = queueClient.getAllQueue(currentUser.getBindClass());
        List<QueueDTO> unShenheQueueList = queueClient.getUnShenheQueue(currentUser.getBindClass());


        Integer queueCount = (queueList != null) ? queueList.size() : 0;
        Integer unShenheQueueCount = (unShenheQueueList != null) ? unShenheQueueList.size() : 0;

        int banwei = ClassCenterUtils.converZhiwuToBanwei(currentUser.getZhiwu());

        List<SayDTO> sayList = sayClient.getSay(currentUser.getBindClass(), banwei);
        List<SayDTO> unReadSayList = sayClient.getUnReadSay(currentUser.getBindClass(), banwei);

        Integer sayCount = (sayList != null) ? sayList.size() : 0;
        Integer unReadSayCount = (unReadSayList != null) ? unReadSayList.size() : 0;

        // 获取登记表完成率
        List<UserCenterTaskFinishRateDTO> registerFinishRate = taskClient.getRegisterFinishRate(currentUser.getUserGroup(), currentUser.getClassId(), currentUser.getBindClass());

        // 获取收集表完成率
        List<UserCenterTaskFinishRateDTO> collectFinishRate = taskClient.getCollectFinishRate(currentUser.getUserGroup(), currentUser.getClassId(), currentUser.getBindClass());

        return Result.success(new AdminHomeDataVO(
                new AdminHomeDataVO.Ty(AllUserCount, TuanyuanUserCount),
                new AdminHomeDataVO.Sh(queueCount, unShenheQueueCount),
                new AdminHomeDataVO.Ly(sayCount, unReadSayCount),
                registerFinishRate,
                collectFinishRate
        ));
    }

    /**
     * 获取所有用户信息
     */
    @Override
    public List<User> getAllUser() {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return null;
        }
        return userRepository.findAllByBindClass(currentUser.getBindClass()).orElse(null);
    }

    /**
     * 获取团员数据
     */
    @Override
    public List<User> getTuanyuanUser() {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return null;
        }
        return userRepository.findAllByZzmmInAndBindClass(Constant.USER_ZZMM, currentUser.getBindClass()).orElse(null);
    }

    /**
     * 通讯录
     */
    @Override
    public Result getClassmates(String prop) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return null;
        }
        List<User> classmatesList;
        if ("ty".equals(prop)) {
            classmatesList = getTuanyuanUser();
        } else if (prop == null || prop.isEmpty()) {
            classmatesList = getAllUser();

        } else {
            return Result.error("参数错误");
        }

        return Result.success(classmatesList);
    }

    @Override
    @Transactional
    public Result manageUsers() {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限操作");
        }

        List<User> users = userRepository.findByBindClass(currentUser.getBindClass());
        return Result.success(users);
    }

    @Override
    @Transactional
    public Result setUserGroup(Integer uid, String group) {
        User currentUser = (User) StpUtil.getSession().get(Constant.USER_SESSION_NAME);
        if (currentUser == null || !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限操作");
        }

        User user = userRepository.findByUidAndBindClass(uid, currentUser.getBindClass());
        if (user == null) {
            return Result.error("该用户不存在");
        }

        user.setUserGroup(group);
        userRepository.save(user);

        return Result.success();
    }

    @Override
    @Transactional
    public Result resetPasswd(Integer uid) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限操作");
        }

        User user = userRepository.findByUidAndBindClass(uid, currentUser.getBindClass());
        if (user == null) {
            return Result.error("该用户不存在");
        }

        AllUser cur_user = allUserRepository.findByClassId(user.getClassId());

        cur_user.setPasswd(Constant.USER_INIT_PASSWORD);
        allUserRepository.save(cur_user);

        StpUtil.logout(cur_user.getClassId());

        return Result.success();
    }

    @Override
    public Result getUserDetail(Integer id) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限操作");
        }

        User user = userRepository.findByUidAndBindClass(id, currentUser.getBindClass());
        if (user == null) {
            return Result.error("该用户不存在");
        }

        return Result.success(user);
    }

    @Override
    @Transactional
    public Result editUser(UserDTO userDTO) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null || !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限操作");
        }

        User user = userRepository.findByClassId(userDTO.getClassid());
        if (user == null) {
            return Result.error("该用户不存在");
        }

        user.setSex(userDTO.getSex());
        user.setTel(userDTO.getTel());
        user.setZhiwu(userDTO.getZhiwu());
        user.setSushe(userDTO.getSushe());
        user.setZzmm(userDTO.getZzmm());
        userRepository.save(user);

        return Result.success();
    }

    @Override
    public Result getUserData() {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        User user = userRepository.findByClassId(currentUser.getClassId());

        Map<String, String> data = new HashMap<>();
        data.put("name", user.getName());
        data.put("classid", user.getClassId());
        data.put("tel", user.getTel());


        AllUser user_info = allUserRepository.findByClassId(currentUser.getClassId());

        boolean ifQQ = user_info.getQqId() != null && !user_info.getQqId().isEmpty();
        boolean ifWX = user_info.getWxId() != null && !user_info.getWxId().isEmpty();

        UserDataVO userDataVO = new UserDataVO(
                user.getName(),
                user.getZhiwu(),
                user.getUserGroup(),
                ifQQ,
                ifWX
        );

        Map<String, Object> response = new HashMap<>();
        response.put("userForm", data);
        response.put("userData", userDataVO);


        return Result.success(response);
    }

    @Override
    @Transactional
    public Result changePassword(String newPasswd) {
        UserInfo currentUser = UserContext.getUser();

        String encodePwd = md5Hash(md5Hash(newPasswd) + Constant.USER_LOGIN_SECRET_KEY);

        AllUser user = allUserRepository.findByClassId(currentUser.getClassId());
        user.setPasswd(encodePwd);
        allUserRepository.save(user);

        StpUtil.logout(currentUser.getClassId());

        return Result.success();
    }

    @Override
    public List<User> getUserList(String bindClass) {
        return userRepository.findByBindClassOrderByIdAsc(bindClass);
    }

    @Override
    public User getUserByClassId(String classId) {
        return userRepository.findByClassId(classId);
    }

    @Override
    public User getUserByUid(Integer uid, String bindClass) {
        return userRepository.findByUidAndBindClass(uid, bindClass);
    }

    @Override
    public List<User> getUserInCollection(Collection<Integer> selectPeoples, String bindClass) {
        return userRepository.findByIdInAndBindClass(selectPeoples, bindClass);
    }

    @Override
    public Result getRandomList() {
        List<User> allUser = getAllUser();

        List<Map<String, String>> personArray = new ArrayList<>();

        allUser.forEach(user -> {
            Map<String, String> personMap = new HashMap<>();
            personMap.put("id", user.getId().toString());
            personMap.put("name", user.getName());
            personArray.add(personMap);
        });

        return Result.success(personArray);
    }
}
