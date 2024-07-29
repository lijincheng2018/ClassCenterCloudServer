package cn.ljcljc.api.client.fallback;

import cn.ljcljc.api.client.TaskClient;
import cn.ljcljc.api.dto.UserCenterTaskFinishRateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;

@Slf4j
public class TaskClientFallbackFactory implements FallbackFactory<TaskClient> {

    @Override
    public TaskClient create(Throwable cause) {
        return new TaskClient() {
            @Override
            public List<UserCenterTaskFinishRateDTO> getCollectFinishRate(String usergroup, String classid, String bindClass) {
                log.error("获取收集表任务完成率失败", cause);
                return Collections.emptyList();
            }

            @Override
            public List<UserCenterTaskFinishRateDTO> getRegisterFinishRate(String usergroup, String classid, String bindClass) {
                log.error("获取登记表任务完成率失败", cause);
                return Collections.emptyList();
            }
        };
    }
}
