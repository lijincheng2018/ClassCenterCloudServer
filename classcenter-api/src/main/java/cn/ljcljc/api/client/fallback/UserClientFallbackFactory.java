package cn.ljcljc.api.client.fallback;

import cn.ljcljc.api.client.UserClient;
import cn.ljcljc.api.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public List<UserDTO> getUserList(String bindClass) {
                log.error("获取用户列表失败", cause);
                return Collections.emptyList();
            }

            @Override
            public UserDTO getUser(String classid) {
                log.error("获取用户失败", cause);
                return new UserDTO();
            }

            @Override
            public UserDTO getUser(Integer uid, String bindClass) {
                log.error("获取用户失败", cause);
                return new UserDTO();
            }

            @Override
            public List<UserDTO> getUserInCollection(Collection<Integer> selectPeoples, String bindClass) {
                log.error("获取用户失败", cause);
                return Collections.emptyList();
            }
        };
    }
}
