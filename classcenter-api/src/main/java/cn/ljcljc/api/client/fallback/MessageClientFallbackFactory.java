package cn.ljcljc.api.client.fallback;

import cn.ljcljc.api.client.MessageClient;
import cn.ljcljc.api.dto.MessagesDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class MessageClientFallbackFactory implements FallbackFactory<MessageClient> {
    @Override
    public MessageClient create(Throwable cause) {
        return new MessageClient() {
            @Override
            public Boolean createMessage(String title, String content, String recipients) {
                log.error("创建消息失败", cause);
                return false;
            }

            @Override
            public List<MessagesDTO> getUnreadMessageList(String uid, String bindClass) {
                log.error("获取未读消息列表失败", cause);
                return Collections.emptyList();
            }
        };
    }
}
