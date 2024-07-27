package cn.ljcljc.common.interceptors;

import cn.hutool.core.util.StrUtil;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取登录用户信息
        String userInfoClassId = request.getHeader("user-info-classid");
        String userInfoBindClassId = request.getHeader("user-info-bindclass");
        String userInfoUserGroup = request.getHeader("user-info-usergroup");
        String userInfoUserName = request.getHeader("user-info-name");
        String userInfoUid = request.getHeader("user-info-uid");
        String userInfoZhiwu = request.getHeader("user-info-zhiwu");
        String ZH_userInfoZhiwu = "";

        if (userInfoZhiwu != null) {
            ZH_userInfoZhiwu = URLDecoder.decode(userInfoZhiwu, StandardCharsets.UTF_8);
        }

        // 2.判断是否获取用户，存入ThreadLocal
        if (StrUtil.isNotBlank(userInfoClassId) && StrUtil.isNotBlank(userInfoBindClassId) && StrUtil.isNotBlank(userInfoUserGroup) && StrUtil.isNotBlank(userInfoUserName) && StrUtil.isNotBlank(userInfoUid) && StrUtil.isNotBlank(ZH_userInfoZhiwu)) {
            UserContext.setUser(new UserInfo(userInfoClassId, Integer.valueOf(userInfoUid), userInfoUserName, userInfoBindClassId, userInfoUserGroup, ZH_userInfoZhiwu));
        }

        // 3.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理用户
        UserContext.removeUser();
    }
}
