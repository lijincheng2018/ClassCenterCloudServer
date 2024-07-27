package cn.ljcljc.common.domain.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String classId;
    private Integer uid;
    private String name;
    private String bindClass;
    private String userGroup;
    private String zhiwu;
}
