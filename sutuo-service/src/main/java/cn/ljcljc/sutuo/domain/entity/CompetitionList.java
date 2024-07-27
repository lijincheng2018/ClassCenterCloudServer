package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionlist")
public class CompetitionList implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cmptName")
    private String cmptName;

    @Column(name = "cmptLevel")
    private String cmptLevel;

    @Column(name = "cmptOrg")
    private String cmptOrg;
}
