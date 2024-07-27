package cn.ljcljc.classfee.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "班费DTO")
public class FeeDTO {
    private String title;
    private String fee;
    private String status;
}
