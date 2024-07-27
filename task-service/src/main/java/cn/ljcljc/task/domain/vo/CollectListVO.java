package cn.ljcljc.task.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CollectListVO {
    private Integer id;
    private String title;
    private String time;
    private String jiezhi_time;
    private boolean ifshenhe;
    private boolean isneed;
    private String poster;
    private List<String> peoples;
    private Integer people_num;
    private long people_num_finish;
}
