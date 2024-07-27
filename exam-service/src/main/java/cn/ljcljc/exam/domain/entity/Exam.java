package cn.ljcljc.exam.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "exam")
public class Exam implements Serializable {
    @Id
    @Column(name = "ExamID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer examID;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "ExamName")
    private String examName;

    @Column(name = "ExamDate")
    private String examDate;

    @Column(name = "CreateTime")
    private String createTime;

    @Column(name = "Subjects")
    private String subjects;

    @Column(name = "ExamLeixing")
    private String examLeixing;

    @Column(name = "isOpen")
    private String isOpen;
}
