package cn.ljcljc.vote.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteListVO {
    private Integer id;
    private Integer unid;
    private String title;
    private Boolean if_finish;
    private String poster;
    private String post_time;
    private String jiezhi_time;
    private Integer status;
    private Boolean isanonymous;
}
