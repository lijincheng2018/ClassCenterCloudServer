package cn.ljcljc.task.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TaskResponseVO {
    private Integer total;
    private Integer finish;
    private List<TaskVO> tasklist;
}
