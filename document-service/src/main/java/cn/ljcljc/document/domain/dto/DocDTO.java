package cn.ljcljc.document.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "个人履历DTO")
public class DocDTO {
    private String title;
    private String leixing;
    private String time;
    private String duration;
    private String region;
}
