package cn.ljcljc.task.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "register_list")
public class RegisterList implements Serializable {
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

    @Column(name = "author")
    private String author;

    @Column(name = "classid")
    private String classId;

    @Column(name = "time")
    private String time;

    @Column(name = "is_public")
    private String isPublic;

    @Column(name = "time_frame")
    private String timeFrame;

    @Column(name = "ifouttime")
    private String ifOutTime;

    @Column(name = "iflimit")
    private String ifLimit;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;
}
