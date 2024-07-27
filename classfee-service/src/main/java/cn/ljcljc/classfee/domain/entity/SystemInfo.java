package cn.ljcljc.classfee.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "system_info")
public class SystemInfo implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "tag")
    private String tag;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "content")
    private String content;
}
