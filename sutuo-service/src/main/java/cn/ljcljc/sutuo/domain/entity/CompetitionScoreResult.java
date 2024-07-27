package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionscoreresult")
public class CompetitionScoreResult implements Serializable {
    @Id
    @Column(name = "classid")
    private String classId;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "competitionYear")
    private Integer competitionYear;

    @Column(name = "totalScore")
    private Double totalScore;

    @Column(name = "totalSuccessScore")
    private Double totalSuccessScore;
}
