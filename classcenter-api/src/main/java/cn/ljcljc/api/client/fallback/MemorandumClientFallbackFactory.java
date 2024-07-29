package cn.ljcljc.api.client.fallback;

import cn.ljcljc.api.client.MemorandumClient;
import cn.ljcljc.api.dto.MemorandumDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class MemorandumClientFallbackFactory implements FallbackFactory<MemorandumClient> {
    @Override
    public MemorandumClient create(Throwable cause) {
        return new MemorandumClient() {
            @Override
            public List<MemorandumDTO> getList(String classid) {
                log.error("获取备忘录失败", cause);
                return Collections.emptyList();
            }
        };
    }
}
