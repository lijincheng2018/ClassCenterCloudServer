package cn.ljcljc.api.client.fallback;

import cn.ljcljc.api.client.NoticeClient;
import cn.ljcljc.api.dto.NoticeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class NoticeClientFallbackFactory implements FallbackFactory<NoticeClient> {
    @Override
    public NoticeClient create(Throwable cause) {
        return new NoticeClient() {
            @Override
            public List<NoticeDTO> getList(String bindClass) {
                log.error("获取公告列表失败", cause);
                return Collections.emptyList();
            }
        };
    }
}
