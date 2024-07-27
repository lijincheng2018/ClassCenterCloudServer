package cn.ljcljc.vote.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "vote")
public class Vote implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "title")
    private String title;

    @Column(name = "people")
    private String people;

    @Column(name = "people_num")
    private Integer peopleNum;

    @Column(name = "select_people")
    private String selectPeople;

    @Column(name = "select_num")
    private Integer selectNum;

    @Column(name = "author")
    private String author;

    @Column(name = "classid")
    private String classId;

    @Column(name = "time")
    private String time;

    @Column(name = "starttime")
    private String startTime;

    @Column(name = "endtime")
    private String endTime;

    @Column(name = "anonymous")
    private Boolean anonymous;
}
