package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "competitionrecords")
public class CompetitionRecords implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "competitionType")
    private Integer competitionType;

    @Column(name = "competitionId")
    private Integer competitionId;

    @Column(name = "competitionClass")
    private Integer competitionClass;

    @Column(name = "competitionRank")
    private Integer competitionRank;

    @Column(name = "competitionLevel")
    private Integer competitionLevel;

    @Column(name = "competitionXingzhi")
    private String competitionXingzhi;

    @Column(name = "competitionYear")
    private Integer competitionYear;

    @Column(name = "status")
    private String status;

    @Column(name = "declarer")
    private String declarer;

    @Column(name = "classid")
    private String classId;

    @Column(name = "score")
    private Double score;

    @Column(name = "proofURL")
    private String proofURL;
}
