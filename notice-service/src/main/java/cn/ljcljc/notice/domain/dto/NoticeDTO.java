package cn.ljcljc.notice.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "公告DTO")
public class NoticeDTO {
    private String title;
    private String content;
}
