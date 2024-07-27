package cn.ljcljc.vote.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VoteDetailVO {
    private Integer id;
    private String title;
    private Boolean if_finish;
    private List<String> select_peoples;
    private Integer select_num;
    private Boolean isanonymous;
    private String poster;
    private Integer status;
}
