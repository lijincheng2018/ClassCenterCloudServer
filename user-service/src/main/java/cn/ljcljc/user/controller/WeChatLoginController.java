package cn.ljcljc.user.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.service.UserAuthService;
import cn.ljcljc.user.service.WeChatLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "微信扫码登录接口", description = "用于处理微信扫码登录相关操作")
@RestController
@RequestMapping("/oauth/wechat")
public class WeChatLoginController {
    private final WeChatLoginService weChatLoginService;
    private final UserAuthService userAuthService;

    @Autowired
    public WeChatLoginController(WeChatLoginService weChatLoginService, UserAuthService userAuthService) {
        this.weChatLoginService = weChatLoginService;
        this.userAuthService = userAuthService;
    }

    @Operation(summary = "网页侧请求获取二维码")
    @GetMapping("/getCode")
    public Result login() {
        return weChatLoginService.createQrcode();
    }

    @Operation(summary = "网页侧轮询扫码状态")
    @GetMapping("/checkStatus")
    public Result getStatus(String scene) {
        return userAuthService.WeChatLoginCheckScanStatus(scene);
    }

    @Operation(summary = "小程序侧通知服务器已扫码")
    @GetMapping("/scanCode")
    public Result scanCode(String scene) {
        return weChatLoginService.updateSceneStatus(scene);
    }

    @Operation(summary = "小程序侧校验身份")
    @GetMapping("/qrlogin")
    public Result login(String code, String scene) {
        return weChatLoginService.loginWithCode(code, scene);
    }

}
