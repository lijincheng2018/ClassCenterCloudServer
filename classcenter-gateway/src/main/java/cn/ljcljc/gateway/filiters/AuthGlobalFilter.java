package cn.ljcljc.gateway.filiters;

import cn.dev33.satoken.stp.StpUtil;
import cn.ljcljc.gateway.config.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request
        ServerHttpRequest request = exchange.getRequest();

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            //true是直接放行
            return chain.filter(exchange);
        }

        // 2.判断是否需要做登录拦截
        if (isExcluede(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        // 3.获取token
        String token = null;
        List<String> headers = request.getHeaders().get("X-Token");
        if (headers != null && !headers.isEmpty()) {
            token = headers.get(0);
        }

        // 4.Sa Token校验登录信息
        String classId = (String) StpUtil.getLoginIdByToken(token);

        if (classId == null) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 5.传递用户信息
        String finalClassId = classId;
        String finalBindClassId = (String) StpUtil.getExtra(token, "bindClass");
        String finalUserGroup = (String) StpUtil.getExtra(token, "usergroup");
        String finalUserName = (String) StpUtil.getExtra(token, "name");

        Object uidObj = StpUtil.getExtra(token, "uid");
        String finalUid = uidObj != null ? uidObj.toString() : null;

        String finalZhiwu = (String) StpUtil.getExtra(token, "zhiwu");
        String enCodeFinalZhiwu = URLEncoder.encode(finalZhiwu, StandardCharsets.UTF_8);
        ServerWebExchange serverWebExchange = exchange.mutate()
                .request(builder -> builder
                        .header("user-info-classid", finalClassId)
                        .header("user-info-bindclass", finalBindClassId)
                        .header("user-info-usergroup", finalUserGroup)
                        .header("user-info-name", finalUserName)
                        .header("user-info-uid", finalUid)
                        .header("user-info-zhiwu", enCodeFinalZhiwu))
                .build();

        // 6.放行
        return chain.filter(serverWebExchange);
    }

    private boolean isExcluede(String path) {
        for (String pathPattern : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(pathPattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
