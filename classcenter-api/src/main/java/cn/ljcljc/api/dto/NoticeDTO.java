package cn.ljcljc.api.dto;

import lombok.Data;

@Data
public class NoticeDTO {
    private Integer id;
    private String bindClass;
    private String title;
    private String content;
    private String author;
    private String classid;
    private String time;
}
