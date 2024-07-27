package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionscoretable")
public class CompetitionScoreTable implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cmptType")
    private String cmptType;

    @Column(name = "cmptClass")
    private String cmptClass;

    @Column(name = "cmptLevel")
    private String cmptLevel;

    @Column(name = "cmptWeight")
    private Double cmptWeight;
}
