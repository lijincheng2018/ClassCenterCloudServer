package cn.ljcljc.user.service;

import cn.ljcljc.common.domain.Result;

public interface WeChatLoginService {
    Result createQrcode();
    Result updateSceneStatus(String scene);
    Result loginWithCode(String code, String scene);
}
