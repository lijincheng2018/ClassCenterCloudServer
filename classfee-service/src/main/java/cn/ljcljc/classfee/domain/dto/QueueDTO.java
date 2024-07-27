package cn.ljcljc.classfee.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "报销审核项DTO")
public class QueueDTO {
    private String ps;
    private String status;
}
