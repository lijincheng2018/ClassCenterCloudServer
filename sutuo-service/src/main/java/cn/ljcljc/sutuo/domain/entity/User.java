package cn.ljcljc.sutuo.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "user")
public class User implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uid")
    private Integer uid;

    @Column(name = "classid")
    private String classId;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "name")
    private String name;

    @Column(name = "sex")
    private String sex;

    @Column(name = "year")
    private String year;

    @Column(name = "xiaoqu")
    private String xiaoqu;

    @Column(name = "xueyuan")
    private String xueyuan;

    @Column(name = "zhuanye")
    private String zhuanye;

    @Column(name = "class")
    private String clazz;

    @Column(name = "tel")
    private String tel;

    @Column(name = "usergroup")
    private String userGroup;

    @Column(name = "zhiwu")
    private String zhiwu;

    @Column(name = "sushe")
    private String sushe;

    @Column(name = "zzmm")
    private String zzmm;
}
