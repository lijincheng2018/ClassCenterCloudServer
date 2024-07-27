package cn.ljcljc.vote.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VoteListAdminVO {
    private Integer id;
    private String title;
    private String time;
    private List<String> limit_time;
    private Boolean isanonymous;
    private String poster;
    private String[] peoples;
    private Integer people_num;
    private String[] select_peoples;
    private Integer select_num;
    private Integer people_num_finish;
}
