package cn.ljcljc.user.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.domain.dto.UserDTO;
import cn.ljcljc.user.domain.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Result getUserHomeData();
    Result getAdminHomeData();
    List<User> getAllUser();
    List<User> getTuanyuanUser();
    Result getClassmates(String prop);
    Result manageUsers();
    Result setUserGroup(Integer uid, String group);
    Result resetPasswd(Integer uid);
    Result getUserDetail(Integer id);
    Result editUser(UserDTO userDTO);
    Result getUserData();
    Result changePassword(String newPasswd);
    List<User> getUserList(String bindClass);
    User getUserByClassId(String classId);
    User getUserByUid(Integer uid, String bindClass);
    List<User> getUserInCollection(Collection<Integer> selectPeoples, String bindClass);
    Result getRandomList();
}
