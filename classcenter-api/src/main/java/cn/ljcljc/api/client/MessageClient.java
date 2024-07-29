package cn.ljcljc.api.client;

import cn.ljcljc.api.client.fallback.MessageClientFallbackFactory;
import cn.ljcljc.api.dto.MessagesDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "message-service", fallbackFactory = MessageClientFallbackFactory.class)
public interface MessageClient {
    @PostMapping("/api/messages/feign/createMessage")
    Boolean createMessage(@RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("recipients") String recipients);

    @GetMapping("/api/messages/feign/getUnreadMessageList")
    List<MessagesDTO> getUnreadMessageList(@RequestParam("uid") String uid, @RequestParam("bindClass") String bindClass);
}
