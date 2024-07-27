package cn.ljcljc.api.dto;

import lombok.Data;

@Data
public class SayDTO {
    private Integer id;
    private String bindClass;
    private Integer banwei;
    private String title;
    private String content;
    private String shiming;
    private String classId;
    private String author;
    private String isRead;
    private String reply;
    private String time;
}
