package cn.ljcljc.task.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "collectdata")
public class CollectData implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uid")
    private Integer uid;

    @Column(name = "classid")
    private String classId;

    @Column(name = "collectid")
    private Integer collectId;

    @Column(name = "name")
    private String name;

    @Column(name = "pd")
    private String status;

    @Column(name = "time")
    private String time;

    @Column(name = "upload_file")
    private String uploadFile;

    @Column(name = "shenhe")
    private String shenhe;
}
