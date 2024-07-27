package cn.ljcljc.user.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户DTO")
public class UserDTO {
    private String classid;
    private String sex;
    private String tel;
    private String zhiwu;
    private String sushe;
    private String zzmm;
}