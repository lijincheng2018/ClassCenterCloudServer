package cn.ljcljc.api.client;

import cn.ljcljc.api.client.fallback.NoticeClientFallbackFactory;
import cn.ljcljc.api.dto.NoticeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "notice-service", fallbackFactory = NoticeClientFallbackFactory.class)
public interface NoticeClient {
    @GetMapping("/api/notices/feign/getList")
    List<NoticeDTO> getList(@RequestParam("bindClass") String bindClass);
}
