package cn.ljcljc.user.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.domain.dto.LoginDTO;
import cn.ljcljc.user.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ljc
 * @since 2024-7-15
 */

@Tag(name = "用户认证接口", description = "用于用户认证")
@RestController
@RequestMapping("/api")
public class UserAuthController {
    private final UserAuthService userAuthService;

    @Autowired
    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @Operation(summary = "用户登录")
    @PostMapping("doLogin")
    @ApiResponse(responseCode = "200", description = "登录成功")
    @ApiResponse(responseCode = "100", description = "登录失败")
    public Result doLogin(@RequestBody LoginDTO loginDTO) {
        return userAuthService.login(loginDTO);
    }

    @Operation(summary = "退出登录")
    @GetMapping("logout")
    public Result logout() {
        return userAuthService.logout();
    }

}
