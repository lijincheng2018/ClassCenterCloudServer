package cn.ljcljc.api.client.fallback;

import cn.ljcljc.api.client.SayClient;
import cn.ljcljc.api.dto.SayDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class SayClientFallbackFactory implements FallbackFactory<SayClient> {

    @Override
    public SayClient create(Throwable cause) {
        return new SayClient() {
            @Override
            public List<SayDTO> getSay(String bindClass, Integer banwei) {
                log.error("获取留言失败", cause);
                return Collections.emptyList();
            }

            @Override
            public List<SayDTO> getUnReadSay(String bindClass, Integer banwei) {
                log.error("获取未读留言失败", cause);
                return Collections.emptyList();
            }
        };
    }
}
