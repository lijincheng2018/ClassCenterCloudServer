package cn.ljcljc.user.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "classroom_info")
public class ClassroomInfo implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "classroomid")
    private String classroomId;

    @Column(name = "classroom_name")
    private String classroomName;

    @Column(name = "data_base")
    private String classroomDB;

    @Column(name = "if_open")
    private String ifOpen;
}
