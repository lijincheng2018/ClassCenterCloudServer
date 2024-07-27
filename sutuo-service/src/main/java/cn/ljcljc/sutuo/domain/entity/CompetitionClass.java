package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionclass")
public class CompetitionClass implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "competitionClass")
    private String competitionClass;

    @Column(name = "weight")
    private Double weight;
}
