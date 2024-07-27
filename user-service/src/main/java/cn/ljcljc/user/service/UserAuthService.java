package cn.ljcljc.user.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.user.domain.dto.LoginDTO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public interface UserAuthService {
    Result login(LoginDTO loginDTO);
    Boolean checkInitialPwd();
    Result logout();
    Result loginByQQ(String code, String redirect, String clientId, String secret, HttpServletResponse response, HttpSession session) throws IOException;
    Result WeChatLoginCheckScanStatus(String scene);
}
