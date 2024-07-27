package cn.ljcljc.api.dto;

import lombok.Data;

@Data
public class MessagesDTO {
    private Integer id;
    private String bindClass;
    private String title;
    private String content;
    private String recipients;
    private String readBy;
    private String poster;
    private String time;
}
