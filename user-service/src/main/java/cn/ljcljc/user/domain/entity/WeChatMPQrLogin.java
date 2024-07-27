package cn.ljcljc.user.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "xcxqrcodelogin")
public class WeChatMPQrLogin implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "scene")
    private String scene;

    @Column(name = "openid")
    private String openid;

    @Column(name = "status")
    private String status;

    @Column(name = "create_time")
    private Timestamp createTime;
}
