package cn.ljcljc.notice.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeAdminVO {
    private Integer id;
    private Integer unid;
    private String title;
    private String content;
    private String poster;
    private String time;
    private Boolean qx;
}
