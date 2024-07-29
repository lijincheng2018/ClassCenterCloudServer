package cn.ljcljc.api.client;

import cn.ljcljc.api.client.fallback.SayClientFallbackFactory;
import cn.ljcljc.api.dto.SayDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "say-service", fallbackFactory = SayClientFallbackFactory.class)
public interface SayClient {
    @GetMapping("/api/say/feign/getSay")
    List<SayDTO> getSay(@RequestParam("bindClass") String bindClass, @RequestParam("banwei") Integer banwei);

    @GetMapping("/api/say/feign/getUnReadSay")
    List<SayDTO> getUnReadSay(@RequestParam("bindClass") String bindClass, @RequestParam("banwei") Integer banwei);
}
