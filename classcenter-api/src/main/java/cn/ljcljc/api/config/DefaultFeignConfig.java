package cn.ljcljc.api.config;

import cn.ljcljc.api.client.fallback.*;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 获取登录用户
                UserInfo user = UserContext.getUser();
                if(user == null) {
                    return;
                }
                // 如果不为空则放入请求头中，传递给下游微服务
                requestTemplate.header("user-info-classid", user.getClassId());
                requestTemplate.header("user-info-bindclass", user.getBindClass());
                requestTemplate.header("user-info-usergroup", user.getUserGroup());
                requestTemplate.header("user-info-name", user.getName());
                requestTemplate.header("user-info-uid", user.getUid().toString());
                requestTemplate.header("user-info-zhiwu", user.getZhiwu());
            }
        };
    }

    @Bean
    public MemorandumClientFallbackFactory memorandumClientFallbackFactory() {
        return new MemorandumClientFallbackFactory();
    }

    @Bean
    public MessageClientFallbackFactory messageClientFallbackFactory() {
        return new MessageClientFallbackFactory();
    }

    @Bean
    public NoticeClientFallbackFactory noticeClientFallbackFactory() {
        return new NoticeClientFallbackFactory();
    }

    @Bean
    public QueueClientFallbackFactory queueClientFallbackFactory() {
        return new QueueClientFallbackFactory();
    }

    @Bean
    public SayClientFallbackFactory sayClientFallbackFactory() {
        return new SayClientFallbackFactory();
    }

    @Bean
    public TaskClientFallbackFactory taskClientFallbackFactory() {
        return new TaskClientFallbackFactory();
    }

    @Bean
    public UserClientFallbackFactory userClientFallbackFactory() {
        return new UserClientFallbackFactory();
    }
}
