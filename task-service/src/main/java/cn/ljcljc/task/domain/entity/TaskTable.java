package cn.ljcljc.task.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "tasktable")
public class TaskTable implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "leixing")
    private String leixing;

    @Column(name = "pid")
    private Integer pid;

    @Column(name = "title")
    private String title;

    @Column(name = "time")
    private String time;
}
