package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionkaoheclass")
public class CompetitionKaoheClass implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "compKaoheClass")
    private String compKaoheClass;

    @Column(name = "weight")
    private Double weight;
}
