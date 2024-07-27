package cn.ljcljc.vote.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteResultVO {
    private Integer id;
    private String name;
    private String classid;
    private String finish_time;
    private String finish_rank;
    private String peo_select_people;
}
