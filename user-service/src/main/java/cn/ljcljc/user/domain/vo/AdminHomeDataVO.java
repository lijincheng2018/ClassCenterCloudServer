package cn.ljcljc.user.domain.vo;

import cn.ljcljc.api.dto.UserCenterTaskFinishRateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理员主页数据视图
 *
 * @author ljc
 * @since 2024-07-16
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminHomeDataVO {

    private Ty ty;
    private Sh sh;
    private Ly ly;
    private List<UserCenterTaskFinishRateDTO> reg;
    private List<UserCenterTaskFinishRateDTO> col;

    // 团员
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ty {
        private Integer allNum;
        private Integer tyNum;
    }

    // 报销审核
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Sh {
        private Integer allNum;
        private Integer unfinishNum;
    }

    // 留言
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ly {
        private Integer allNum;
        private Integer unreadNum;
    }
}
