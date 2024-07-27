package cn.ljcljc.user.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.domain.dto.UserDTO;
import cn.ljcljc.user.domain.entity.User;
import cn.ljcljc.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * @author ljc
 * @since 2024-7-16
 */

@Tag(name = "用户数据接口", description = "用于用户数据的查询")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取首页用户基本数据")
    @GetMapping("/getHomeUserData")
    public Result getHomeUserData() {
        return userService.getUserHomeData();
    }

    @Operation(summary = "获取首页管理员基本数据")
    @GetMapping("/getHomeAdminData")
    public Result getHomeAdminData() {
        return userService.getAdminHomeData();
    }

    @Operation(summary = "管理员获取用户列表")
    @GetMapping("/manage")
    public Result manageUsers() {
        return userService.manageUsers();
    }

    @Operation(summary = "管理员设置用户组")
    @PutMapping("/setUserGroup/{uid}")
    public Result setUserGroup(@PathVariable Integer uid, @RequestParam String group) {
        return userService.setUserGroup(uid, group);
    }

    @Operation(summary = "管理员重置用户密码")
    @PutMapping("/resetPasswd/{uid}")
    public Result resetPasswd(@PathVariable Integer uid) {
        return userService.resetPasswd(uid);
    }

    @Operation(summary = "管理员获取用户详情")
    @GetMapping("/getDetail/{uid}")
    public Result getUserDetail(@PathVariable Integer uid) {
        return userService.getUserDetail(uid);
    }

    @Operation(summary = "管理员编辑用户信息")
    @PutMapping("/edit")
    public Result editUser(@RequestBody UserDTO userDTO) {
        return userService.editUser(userDTO);
    }

    @Operation(summary = "用户获取个人信息")
    @GetMapping("/getUserData")
    public Result getUserData() {
        return userService.getUserData();
    }

    @Operation(summary = "用户修改密码")
    @PutMapping("/changePassword")
    public Result changePassword(@RequestParam String newPasswd) {
        return userService.changePassword(newPasswd);
    }

    @Operation(summary = "获取用户列表-微服务远程调用接口")
    @GetMapping("/feign/getUserList")
    public List<User> getUserList(@RequestParam String bindClass) {
        return userService.getUserList(bindClass);
    }

    @Operation(summary = "用学号获取用户信息-微服务远程调用接口")
    @GetMapping("/feign/getUserByClassId")
    public User getUserByClassId(@RequestParam String classid) {
        return userService.getUserByClassId(classid);
    }

    @Operation(summary = "用班级号数获取用户信息-微服务远程调用接口")
    @GetMapping("/feign/getUserByUid")
    public User getUserByUid(@RequestParam Integer uid, @RequestParam String bindClass) {
        return userService.getUserByUid(uid, bindClass);
    }

    @Operation(summary = "查询在某集合内的用户列表-微服务远程调用接口")
    @GetMapping("/feign/getUserInCollection")
    public List<User> getUserInCollection(@RequestParam Collection<Integer> selectPeoples, @RequestParam String bindClass) {
        return userService.getUserInCollection(selectPeoples, bindClass);
    }

    @Operation(summary = "获取学生列表", description = "根据 prop 参数获取不同的通讯录。如果 prop 为空，则获取班级通讯录；如果 prop 为 'ty'，则获取团员列表。")
    @GetMapping("/getClassmateList")
    public Result getClassmateList(
            @Parameter(description = "查询类型，默认为班级通讯录，如果为 'ty' 则获取团员列表")
            @RequestParam(value = "prop", required = false) String prop) {
        return userService.getClassmates(prop);
    }
    @Operation(summary = "获取抽号列表")
    @GetMapping("/getRandomList")
    public Result getRandomList() {
        return userService.getRandomList();
    }
}
