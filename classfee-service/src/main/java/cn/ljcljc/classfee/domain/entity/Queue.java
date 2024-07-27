package cn.ljcljc.classfee.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "queue")
public class Queue implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "title")
    private String title;

    @Column(name = "yt")
    private String yt;

    @Column(name = "fee")
    private String fee;

    @Column(name = "payment")
    private String payment;

    @Column(name = "method")
    private String method;

    @Column(name = "photo1")
    private String photo1;

    @Column(name = "photo2")
    private String photo2;

    @Column(name = "classid")
    private String classId;

    @Column(name = "author")
    private String author;

    @Column(name = "time")
    private String time;

    @Column(name = "xiaofei_time")
    private String xiaofeiTime;

    @Column(name = "ps")
    private String ps;

    @Column(name = "pf_author")
    private String pfAuthor;

    @Column(name = "pf_time")
    private String pfTime;
}
