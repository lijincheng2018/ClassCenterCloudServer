package cn.ljcljc.vote.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "投票DTO")
public class VoteDTO {
    private String title;
    private String people;
    private String select_people;
    private String start_time;
    private String end_time;
    private Boolean isanonymous;
    private Integer select_num;
}
