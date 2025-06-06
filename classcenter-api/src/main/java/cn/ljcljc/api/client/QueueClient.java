package cn.ljcljc.api.client;

import cn.ljcljc.api.client.fallback.QueueClientFallbackFactory;
import cn.ljcljc.api.dto.QueueDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "classfee-service", fallbackFactory = QueueClientFallbackFactory.class)
public interface QueueClient {
    @GetMapping("/api/queue/feign/getAllQueue")
    List<QueueDTO> getAllQueue(@RequestParam("bindClass") String bindClass);

    @GetMapping("/api/queue/feign/getUnShenheQueue")
    List<QueueDTO> getUnShenheQueue(@RequestParam("bindClass") String bindClass);
}
