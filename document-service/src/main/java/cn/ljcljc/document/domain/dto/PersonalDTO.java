package cn.ljcljc.document.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人荣誉DTO")
public class PersonalDTO {
    private String title;
    private String leixing;
    private String rank;
    private String time;
}
