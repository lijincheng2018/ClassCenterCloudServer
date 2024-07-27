package cn.ljcljc.classfee.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeVO {
    private Integer id;

    private String title;

    private String money;

    private String after_money;

    private String method;

    private String poster;

    private String time;
}
