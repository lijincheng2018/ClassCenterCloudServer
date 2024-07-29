package cn.ljcljc.api.client.fallback;

import cn.ljcljc.api.client.QueueClient;
import cn.ljcljc.api.dto.QueueDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class QueueClientFallbackFactory implements FallbackFactory<QueueClient> {
    @Override
    public QueueClient create(Throwable cause) {
        return new QueueClient() {
            @Override
            public List<QueueDTO> getAllQueue(String bindClass) {
                log.error("获取队列失败", cause);
                return Collections.emptyList();
            }

            @Override
            public List<QueueDTO> getUnShenheQueue(String bindClass) {
                log.error("获取未审核队列失败", cause);
                return Collections.emptyList();
            }
        };
    }
}
