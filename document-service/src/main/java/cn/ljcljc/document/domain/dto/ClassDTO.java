package cn.ljcljc.document.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "班级荣誉DTO")
public class ClassDTO {
    private String title;
    private String time;
    private String leixing;
    private String rank;
    private String people;
}
