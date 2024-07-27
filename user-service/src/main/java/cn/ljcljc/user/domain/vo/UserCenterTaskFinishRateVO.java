package cn.ljcljc.user.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页登记表、登记表完成率展示视图
 *
 *  @author ljc
 *  @since 2024-07-16
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCenterTaskFinishRateVO {
    private String title;
    private String finish_rate;
    private String finish_jd;
}
