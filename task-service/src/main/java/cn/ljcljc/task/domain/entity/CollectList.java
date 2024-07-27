package cn.ljcljc.task.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "collect_list")
public class CollectList implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bindClass")
    private String bindClass;

    @Column(name = "title")
    private String title;

    @Column(name = "people")
    private String people;

    @Column(name = "people_num")
    private Integer peopleNum;

    @Column(name = "file_rename")
    private String fileRename;

    @Column(name = "ifrename")
    private String ifRename;

    @Column(name = "folder_name")
    private String folderName;

    @Column(name = "if_folder")
    private String ifFolder;

    @Column(name = "ifrank")
    private String ifRank;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "file_format_num")
    private Integer fileFormatNum;

    @Column(name = "author")
    private String author;

    @Column(name = "classid")
    private String classId;

    @Column(name = "bond")
    private String bond;

    @Column(name = "time")
    private String time;

    @Column(name = "notice")
    private String notice;

    @Column(name = "time_frame")
    private String timeFrame;

    @Column(name = "ifouttime")
    private String ifOutTime;

    @Column(name = "iflimit")
    private String ifLimit;

    @Column(name = "start_time")
    private String startTime;

    @Column(name = "end_time")
    private String endTime;

    @Column(name = "ifshenhe")
    private String ifShenhe;

    @Column(name = "isneed")
    private String isNeed;
}
