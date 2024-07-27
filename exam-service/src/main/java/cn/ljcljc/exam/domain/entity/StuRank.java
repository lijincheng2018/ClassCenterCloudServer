package cn.ljcljc.exam.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "stu_rank")
public class StuRank implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "exam_id")
    private Integer examId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "class_rank")
    private String classRank;

    @Column(name = "zhuanye_rank")
    private String zhuanyeRank;

    @Column(name = "zonghe")
    private String zonghe;
}
