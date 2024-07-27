package cn.ljcljc.exam.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "subject_list")
public class Subject implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "subject_prop")
    private String subjectProp;

    @Column(name = "subject_name")
    private String subjectName;
}
