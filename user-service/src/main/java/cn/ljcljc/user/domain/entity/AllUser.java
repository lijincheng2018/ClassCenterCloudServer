package cn.ljcljc.user.domain.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "all_users")
public class AllUser implements Serializable {

    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

    @Column(name = "classid")
    private String classId;

    @Column(name = "passwd")
    private String passwd;

    @Column(name = "qqid")
    private String qqId;

    @Column(name = "wxid")
    private String wxId;

    @Column(name = "private_wxid")
    private String privateWxId;

    @Column(name = "name")
    private String name;

    @Column(name = "classroomid")
    private String classroomId;
}
