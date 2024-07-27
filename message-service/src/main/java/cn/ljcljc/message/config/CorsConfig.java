package cn.ljcljc.message.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //允许跨域访问资源定义
        registry.addMapping("/**")
                //(只允许本地的指定端口访问)允许所有
                .allowedOrigins("*")
                // 允许所有方法
                .allowedMethods("*")
                //允许所有请求头
                .allowedHeaders("*")
                .maxAge(3600);
    }
}