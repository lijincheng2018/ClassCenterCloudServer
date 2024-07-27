package cn.ljcljc.document.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalVO {
    private Integer id;
    private Integer unid;
    private String title;
    private String time;
    private String rank;
    private String poster;
    private String leixing;
    private String classid;
}
