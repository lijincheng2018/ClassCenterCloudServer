package cn.ljcljc.career.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "careerplanninglist")
public class CareerPlanningList implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "classid")
    private String classid;

    @Column(name = "reportId")
    private String reportId;

    @Column(name = "time")
    private String time;
}
