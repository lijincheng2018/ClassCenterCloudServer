package cn.ljcljc.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.service.UserAuthService;
import cn.ljcljc.common.utils.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@Tag(name = "QQ登录接口", description = "用于处理QQ登录认证操作")
@Slf4j
@RestController
@RequestMapping("/oauth")
public class QQLoginController {

    private final UserAuthService userAuthService;
    @Value("${qq.redirect}")
    private String redirect;
    @Value("${qq.appid}")
    private String clientId;
    @Value("${qq.appkey}")
    private String secret;

    @Autowired
    public QQLoginController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    /**
     * 请求QQ登录
     */
    @Operation(summary = "请求QQ登录接口")
    @GetMapping("/QQlogin")
    public void loginByQq(@RequestParam("redirectTo") String redirectTo, HttpServletResponse response, HttpSession session) throws Exception {
        // QQ回调URL
        session.setAttribute("redirectTo", redirectTo);
        // 请求QQ认证服务器
        response.sendRedirect(String.format("%s?response_type=code&client_id=%s&redirect_uri=%s", Constant.QQ_AUTHORIZE_URL, clientId, redirect));
    }

    @Operation(summary = "绑定QQ登录接口")
    @GetMapping("/BindQQ")
    public void bindQq(@RequestParam(required = false) String redirectTo, @RequestParam(required = false) String token, HttpServletResponse response, HttpSession session) throws Exception {

        if (token == null || redirectTo == null) {
            throw new Exception("参数不完整");
        }
        session.setAttribute("redirectTo", redirectTo);
        session.setAttribute("bind", true);


        String classid = (String) StpUtil.getLoginIdByToken(token);
        if (classid == null) {
            throw new Exception("参数错误");
        }

        session.setAttribute("classid", classid);
        // 请求QQ认证服务器
        response.sendRedirect(String.format("%s?response_type=code&client_id=%s&redirect_uri=%s", Constant.QQ_AUTHORIZE_URL, clientId, redirect));
    }

    /**
     * QQ登录回调
     */
    @Operation(summary = "QQ登录回调接口")
    @GetMapping("/qq")
    public Result connection(@RequestParam("code") String code, HttpServletResponse response, HttpSession session) throws IOException {
        return userAuthService.loginByQQ(code, redirect, clientId, secret, response, session);
    }
}
