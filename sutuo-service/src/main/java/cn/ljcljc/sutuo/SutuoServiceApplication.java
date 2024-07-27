package cn.ljcljc.sutuo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "cn.ljcljc.api.client")
@SpringBootApplication
public class SutuoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SutuoServiceApplication.class, args);
    }
}
