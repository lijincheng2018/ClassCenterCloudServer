package cn.ljcljc.exam.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "score")
public class Score implements Serializable {
    @Id
    @Column(name = "ScoreID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ExamID")
    private Integer examID;

    @Column(name = "StudentID")
    private String studentID;

    @Column(name = "SubjectName")
    private String subjectName;

    @Column(name = "SubjectScore")
    private String score;
}
