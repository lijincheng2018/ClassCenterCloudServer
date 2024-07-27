package cn.ljcljc.user.domain.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登录DTO")
public class LoginDTO {
    @Parameter(name = "用户名")
    private String username;

    @Parameter(name = "密码")
    private String password;
}
