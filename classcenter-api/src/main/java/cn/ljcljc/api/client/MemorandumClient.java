package cn.ljcljc.api.client;

import cn.ljcljc.api.dto.MemorandumDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("memorandum-service")
public interface MemorandumClient {
    @GetMapping("/api/memorandum/feign/getList")
    List<MemorandumDTO> getList(@RequestParam("classid") String classid);
}

