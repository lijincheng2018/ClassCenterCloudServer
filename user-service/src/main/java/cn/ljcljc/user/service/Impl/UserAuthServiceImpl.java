package cn.ljcljc.user.service.Impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.domain.dto.LoginDTO;
import cn.ljcljc.user.domain.entity.AllUser;
import cn.ljcljc.user.domain.entity.ClassroomInfo;
import cn.ljcljc.user.domain.entity.User;
import cn.ljcljc.user.domain.entity.WeChatMPQrLogin;
import cn.ljcljc.user.repository.AllUserRepository;
import cn.ljcljc.user.repository.ClassroomInfoRepository;
import cn.ljcljc.user.repository.UserRepository;
import cn.ljcljc.user.repository.WeChatMPQrLoginRepository;
import cn.ljcljc.user.service.UserAuthService;
import cn.ljcljc.common.utils.Constant;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ljc
 * @since 2024-7-15
 */

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final AllUserRepository allUserRepository;
    private final UserRepository userRepository;
    private final ClassroomInfoRepository classroomInfoRepository;
    private final WeChatMPQrLoginRepository weChatMPQrLoginRepository;

    @Override
    public Result login(LoginDTO loginDTO) {
//        loginDTO.setPassword(md5Hash(md5Hash(loginDTO.getPassword()) + Constant.USER_LOGIN_SECRET_KEY));
        AllUser user = allUserRepository.findByClassIdAndPasswd(loginDTO.getUsername(), loginDTO.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        ClassroomInfo classroomInfo = classroomInfoRepository.findByClassroomId(user.getClassroomId()).orElse(null);
        if (classroomInfo != null && classroomInfo.getIfOpen().equals("0")) {
            return Result.error("该班级未开放");
        }
        User cur_user = userRepository.findByClassId(user.getClassId());

        SaLoginModel loginModel = new SaLoginModel();
        loginModel.setExtra("classid", user.getClassId());
        loginModel.setExtra("name", user.getName());
        loginModel.setExtra("bindClass", cur_user.getBindClass());
        loginModel.setExtra("usergroup", cur_user.getUserGroup());
        loginModel.setExtra("uid", cur_user.getUid());
        loginModel.setExtra("zhiwu", cur_user.getZhiwu());

        StpUtil.login(user.getClassId(), loginModel);
        String tokenValue = StpUtil.getTokenInfo().getTokenValue();


        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("token", tokenValue);
        responseMap.put("username", user.getName());

        return Result.success(responseMap);
    }

    @Override
    public Boolean checkInitialPwd() {
        String loginId = (String) StpUtil.getLoginId();
        AllUser user = allUserRepository.findByClassId(loginId);
        if (user == null) {
            return false;
        }
        return Constant.USER_INIT_PASSWORD.equals(user.getPasswd());
    }

    @Override
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

    @Override
    @Transactional
    public Result loginByQQ(String code, String redirect, String clientId, String secret, HttpServletResponse response, HttpSession session) throws IOException {
        // 向QQ认证服务器申请令牌
        String param = String.format("grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&client_secret=%s",
                code, redirect, clientId, secret);
        String result = HttpUtil.post(Constant.QQ_AUTHORIZE_TOKEN_URL, param);
        Map<String, String> params = params2Map(result);
        String accessToken = params.get("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            return Result.error("登录失败：未获取到AccessToken！");
        }
        // 获取QQ用户的openId
        String openIdResp = HttpUtil.get(String.format("%s?access_token=%s", Constant.QQ_AUTHORIZE_OPENID_URL, accessToken));
        JSON parse = JSONUtil.parse(openIdResp.substring(openIdResp.indexOf("{"), openIdResp.indexOf("}") + 1));
        String openId = (String) parse.getByPath("openid");
        if (openId == null || openId.isEmpty()) {
            return Result.error("登录失败：未获取到OpenId！");
        }
        String redirectTo = (String) session.getAttribute("redirectTo");

        if (session.getAttribute("bind") != null && (boolean) session.getAttribute("bind")) {
            AllUser aUser = allUserRepository.findByClassId((String) session.getAttribute("classid"));
            aUser.setQqId(openId);
            allUserRepository.save(aUser);

            session.invalidate();
            response.sendRedirect("https://class.ljcljc.cn" + redirectTo);
            return Result.success();
        }

        // 根据OpenID获取QQ用户
        AllUser user = allUserRepository.findByQqId(openId);

        if (user == null) {
            session.invalidate();
            response.sendRedirect("https://class.ljcljc.cn/noQQ");
            return Result.error("登录失败：未获取到QQ用户！");
        }

        User cur_user = userRepository.findByClassId(user.getClassId());

        SaLoginModel loginModel = new SaLoginModel();
        loginModel.setExtra("classid", user.getClassId());
        loginModel.setExtra("name", user.getName());
        loginModel.setExtra("bindClass", cur_user.getBindClass());
        loginModel.setExtra("usergroup", cur_user.getUserGroup());
        loginModel.setExtra("uid", cur_user.getUid());
        loginModel.setExtra("zhiwu", cur_user.getZhiwu());

        StpUtil.login(user.getClassId(), loginModel);
        String tokenValue = StpUtil.getTokenInfo().getTokenValue();


        session.invalidate();

        response.sendRedirect("https://class.ljcljc.cn/qqlogin?redirect=" + redirectTo + "&token=" + tokenValue + "&name=" + URLEncoder.encode(user.getName(), StandardCharsets.UTF_8));
        return Result.success(tokenValue);
    }

    @Override
    public Result WeChatLoginCheckScanStatus(String scene) {
        WeChatMPQrLogin existingScene = weChatMPQrLoginRepository.findByScene(scene);

        if (existingScene == null) {
            return Result.error("请刷新重试");
        }

        String status = existingScene.getStatus();
        String openid = existingScene.getOpenid();

        if ("1".equals(status)) {
            return Result.error("请使用微信扫码");
        } else if ("2".equals(status)) {
            return Result.error("已扫码，请授权登录");
        } else if ("3".equals(status)) {
            AllUser user = allUserRepository.findByWxId(openid);
            if (user == null) {
                return Result.error("用户信息未找到");
            }

            String classroomId = user.getClassroomId();

            ClassroomInfo classroomInfo = classroomInfoRepository.findByClassroomId(classroomId).orElse(null);
            if (classroomInfo == null) {
                return Result.error("班级信息未找到");
            }

            if ("0".equals(classroomInfo.getIfOpen())) {
                return Result.error("该班级暂未开通服务");
            }
            User cur_user = userRepository.findByClassId(user.getClassId());

            SaLoginModel loginModel = new SaLoginModel();
            loginModel.setExtra("classid", user.getClassId());
            loginModel.setExtra("name", user.getName());
            loginModel.setExtra("bindClass", cur_user.getBindClass());
            loginModel.setExtra("usergroup", cur_user.getUserGroup());
            loginModel.setExtra("zhiwu", cur_user.getZhiwu());
            loginModel.setExtra("uid", cur_user.getUid());


            StpUtil.login(user.getClassId(), loginModel);
            String tokenValue = StpUtil.getTokenInfo().getTokenValue();


            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("token", tokenValue);
            responseMap.put("username", user.getName());


            return Result.success(responseMap);
        }

        return Result.error("未知状态");
    }

    private Map<String, String> params2Map(String params) {
        Map<String, String> map = new HashMap<>();
        String[] tmp = params.trim().split("&");
        for (String param : tmp) {
            String[] kv = param.split("=");
            if (kv.length == 2) {
                map.put(kv[0], kv[1]);
            }
        }
        return map;
    }
}
