package cn.ljcljc.vote.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "votedata")
public class VoteData implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pid")
    private Integer pid;

    @Column(name = "classid")
    private String classId;

    @Column(name = "voteTo")
    private String voteTo;

    @Column(name = "time")
    private String time;
}
