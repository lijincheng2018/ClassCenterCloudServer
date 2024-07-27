package cn.ljcljc.say.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "say")
public class Say implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "banwei")
    private Integer banwei;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "shiming")
    private String shiming;

    @Column(name = "classid")
    private String classId;

    @Column(name = "author")
    private String author;

    @Column(name = "isread")
    private String isRead;

    @Column(name = "reply")
    private String reply;

    @Column(name = "time")
    private String time;
}
