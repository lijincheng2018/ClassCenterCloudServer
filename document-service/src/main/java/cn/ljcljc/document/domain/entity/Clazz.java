package cn.ljcljc.document.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "class")
public class Clazz implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "title")
    private String title;

    @Column(name = "mc")
    private String mc;

    @Column(name = "dengji")
    private String dengji;

    @Column(name = "time")
    private String time;

    @Column(name = "people")
    private String people;

    @Column(name = "people_num")
    private Integer peopleNum;

    @Column(name = "author")
    private String author;
}
