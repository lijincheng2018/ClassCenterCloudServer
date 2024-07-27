package cn.ljcljc.exam.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreReportVO {
    private Integer id;

    private Integer examID;

    private String studentID;

    private String subjectName;

    private String subjectCNName;

    private String score;
}
