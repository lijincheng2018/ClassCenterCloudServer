package cn.ljcljc.task.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskVO {
    private Integer id;
    private Integer unid;
    private String title;
    private boolean ifFinish;
    private String leixing;
    private String poster;
    private String postTime;
    private boolean ifLimit;
    private String startTime;
    private String endTime;
    private String jieZhiTime;
    private int status;
    private boolean ifShenhe;
    private boolean isNeed;

    public TaskVO(Integer id, Integer unid, String title, boolean ifFinish, String leixing, String poster,
                  String postTime, boolean ifLimit, String startTime, String endTime, int status) {
        this.id = id;
        this.unid = unid;
        this.title = title;
        this.ifFinish = ifFinish;
        this.leixing = leixing;
        this.poster = poster;
        this.postTime = postTime;
        this.ifLimit = ifLimit;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public TaskVO(Integer id, Integer unid, String title, boolean ifFinish, String leixing, String poster,
                  String postTime, boolean ifLimit, String jieZhiTime, int status) {
        this.id = id;
        this.unid = unid;
        this.title = title;
        this.ifFinish = ifFinish;
        this.leixing = leixing;
        this.poster = poster;
        this.postTime = postTime;
        this.ifLimit = ifLimit;
        this.jieZhiTime = jieZhiTime;
        this.status = status;
    }

    public TaskVO(Integer id, Integer unid, String title, boolean ifFinish, String leixing, String poster,
                  String postTime, String jieZhiTime, int status) {
        this.id = id;
        this.unid = unid;
        this.title = title;
        this.ifFinish = ifFinish;
        this.leixing = leixing;
        this.poster = poster;
        this.postTime = postTime;
        this.jieZhiTime = jieZhiTime;
        this.status = status;
    }

    public TaskVO(Integer id, Integer unid, String title, boolean ifFinish, String leixing, String poster,
                  String postTime, boolean ifLimit, String jieZhiTime, int status, boolean ifShenhe, boolean isNeed) {
        this.id = id;
        this.unid = unid;
        this.title = title;
        this.ifFinish = ifFinish;
        this.leixing = leixing;
        this.poster = poster;
        this.postTime = postTime;
        this.ifLimit = ifLimit;
        this.jieZhiTime = jieZhiTime;
        this.status = status;
        this.ifShenhe = ifShenhe;
        this.isNeed = isNeed;
    }

    public TaskVO(Integer id, Integer unid, String title, boolean ifFinish, String leixing, String poster,
                  String postTime, boolean ifLimit, String startTime, String endTime, int status, boolean ifShenhe, boolean isNeed) {
        this.id = id;
        this.unid = unid;
        this.title = title;
        this.ifFinish = ifFinish;
        this.leixing = leixing;
        this.poster = poster;
        this.postTime = postTime;
        this.ifLimit = ifLimit;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.ifShenhe = ifShenhe;
        this.isNeed = isNeed;
    }

    public TaskVO(Integer id, Integer unid, String title, boolean ifFinish, String leixing, String poster,
                  String postTime, String jieZhiTime, int status, boolean ifShenhe, boolean isNeed) {
        this.id = id;
        this.unid = unid;
        this.title = title;
        this.ifFinish = ifFinish;
        this.leixing = leixing;
        this.poster = poster;
        this.postTime = postTime;
        this.jieZhiTime = jieZhiTime;
        this.status = status;
        this.ifShenhe = ifShenhe;
        this.isNeed = isNeed;
    }
}
