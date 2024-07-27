package cn.ljcljc.message.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.message.domain.entity.Messages;

import java.util.List;

public interface MessageService {
    Result getContent(Integer id);

    Result getUnreadNum();

    Result listMessages();

    Boolean createMessage(String title, String content, String recipients);

    List<Messages> listFeignUnreadMessages(Integer uid, String bindClass);
}
