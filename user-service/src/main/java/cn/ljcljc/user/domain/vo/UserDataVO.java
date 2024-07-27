package cn.ljcljc.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDataVO {
    private String name;
    private String zhiwu;
    private String qx;
    private boolean ifQQ;
    private boolean ifWX;
}
