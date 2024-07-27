package cn.ljcljc.document.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 班级荣誉数据视图
 *
 * @author ljc
 * @since 2024-07-16
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassVO {
    private Integer id;
    private Integer unid;
    private String title;
    private String time;
    private String rank;
    private String poster;
    private String leixing;
    private String classid;
    private List<Integer> peoples;
    private int peopleNum;
}
