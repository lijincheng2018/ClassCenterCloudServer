package cn.ljcljc.api.client;

import cn.ljcljc.api.client.fallback.TaskClientFallbackFactory;
import cn.ljcljc.api.dto.UserCenterTaskFinishRateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "task-service", fallbackFactory = TaskClientFallbackFactory.class)
public interface TaskClient {
    @GetMapping("/api/collect/feign/getCollectFinishRate")
    List<UserCenterTaskFinishRateDTO> getCollectFinishRate(@RequestParam("usergroup") String usergroup, @RequestParam("classid") String classid, @RequestParam("bindClass") String bindClass);

    @GetMapping("/api/register/feign/getRegisterFinishRate")
    List<UserCenterTaskFinishRateDTO> getRegisterFinishRate(@RequestParam("usergroup") String usergroup, @RequestParam("classid") String classid, @RequestParam("bindClass") String bindClass);
}
