package cn.ljcljc.user.domain.vo;

import cn.ljcljc.api.dto.MemorandumDTO;
import cn.ljcljc.api.dto.NoticeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用户主页数据视图
 *
 * @author ljc
 * @since 2024-07-16
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserHomeDataVO {
    private String usergroup;
    private String zhiwu;
    private List<NoticeDTO> notice;
    private String clazz;
    private Integer unreadnum;
    private List<MemorandumDTO> beiwanglu;
    private Boolean change_passwd;
}
