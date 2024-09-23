package cn.ljcljc.sutuo;

import cn.ljcljc.api.config.DefaultFeignConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "cn.ljcljc.api.client", defaultConfiguration = DefaultFeignConfig.class)
@SpringBootApplication
public class SutuoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SutuoServiceApplication.class, args);
    }
}
