package cn.ljcljc.document.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "doc")
public class Doc implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "r_time")
    private String rTime;

    @Column(name = "dengji")
    private String dengji;

    @Column(name = "time")
    private String time;

    @Column(name = "place")
    private String place;

    @Column(name = "classid")
    private String classId;

    @Column(name = "author")
    private String author;
}
