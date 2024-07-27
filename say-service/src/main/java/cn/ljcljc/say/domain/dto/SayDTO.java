package cn.ljcljc.say.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "留言DTO")
public class SayDTO {
    private String title;
    private String content;
    private String banwei;
    private String shiming;
}
