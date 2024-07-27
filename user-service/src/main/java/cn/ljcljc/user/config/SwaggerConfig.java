package cn.ljcljc.user.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addParameters("X-Token", new Parameter()
                                .in(ParameterIn.HEADER.toString())
                                .name("X-Token")
                                .description("令牌Token")
                                .required(false)))
                .info(
                        new Info()
                                .title("班级信息中心云平台")
                                .description("班级信息中心云平台 Java版本接口文档")
                                .version("v1.0.0")
                );
    }

    @Bean
    public OperationCustomizer addHeaderParam() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            operation.addParametersItem(new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .name("X-Token")
                    .description("令牌Token")
                    .required(false));
            return operation;
        };
    }
}
