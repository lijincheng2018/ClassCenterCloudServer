package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionyear")
public class CompetitionYear implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "competitionYear")
    private String competitionYear;

    @Column(name = "openTime")
    private String openTime;

    @Column(name = "closeTime")
    private String closeTime;
}
