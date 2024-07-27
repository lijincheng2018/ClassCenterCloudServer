package cn.ljcljc.api.client;

import cn.ljcljc.api.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient("user-service")
public interface UserClient {
    @GetMapping("/api/users/feign/getUserList")
    List<UserDTO> getUserList(@RequestParam("bindClass") String bindClass);

    @GetMapping("/api/users/feign/getUserByClassId")
    UserDTO getUser(@RequestParam("classid") String classid);

    @GetMapping("/api/users/feign/getUserByUid")
    UserDTO getUser(@RequestParam("uid") Integer uid, @RequestParam("bindClass") String bindClass);

    @GetMapping("/api/users/feign/getUserInCollection")
    List<UserDTO> getUserInCollection(@RequestParam("selectPeoples") Collection<Integer> selectPeoples, @RequestParam("bindClass") String bindClass);
}
