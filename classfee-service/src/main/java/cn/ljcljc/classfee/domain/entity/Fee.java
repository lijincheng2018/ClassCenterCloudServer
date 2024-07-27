package cn.ljcljc.classfee.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "fee")
public class Fee implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "title")
    private String title;

    @Column(name = "fee")
    private String fee;

    @Column(name = "after_f")
    private String afterMoney;

    @Column(name = "method")
    private String method;

    @Column(name = "author")
    private String author;

    @Column(name = "time")
    private String time;
}
