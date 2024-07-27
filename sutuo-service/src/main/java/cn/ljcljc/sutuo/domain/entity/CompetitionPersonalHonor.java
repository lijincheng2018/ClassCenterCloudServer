package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionpersonalhonor")
public class CompetitionPersonalHonor implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "honorName")
    private String honorName;

    @Column(name = "honorWeight")
    private Double honorWeight;
}
