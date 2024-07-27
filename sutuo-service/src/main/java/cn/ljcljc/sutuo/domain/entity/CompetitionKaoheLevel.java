package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionkaohelevel")
public class CompetitionKaoheLevel implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "compKaoheLevel")
    private String compKaoheLevel;

    @Column(name = "score")
    private Double score;
}
