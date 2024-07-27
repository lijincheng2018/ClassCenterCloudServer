package cn.ljcljc.message.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "messages")
public class Messages implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "recipients")
    private String recipients;

    @Column(name = "read_by")
    private String readBy;

    @Column(name = "poster")
    private String poster;

    @Column(name = "time")
    private String time;

}
