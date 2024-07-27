package cn.ljcljc.task.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "登记表DTO")
public class RegisterDTO {
    private String title;
    private String people;
    private String ifallow;
    private String jiezhi_time;
    private String ifouttime;
    private String iflimit;
    private String start_time;
    private String end_time;
}
