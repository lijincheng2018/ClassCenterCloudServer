package cn.ljcljc.exam.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.exam.domain.entity.Exam;
import cn.ljcljc.exam.domain.entity.Score;
import cn.ljcljc.exam.domain.entity.StuRank;
import cn.ljcljc.exam.domain.entity.Subject;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.exam.domain.vo.ScoreReportVO;
import cn.ljcljc.exam.repository.ExamRepository;
import cn.ljcljc.exam.repository.ScoreRepository;
import cn.ljcljc.exam.repository.StuRankRepository;
import cn.ljcljc.exam.repository.SubjectRepository;
import cn.ljcljc.exam.service.ExamService;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ljc
 * @since 2024-7-18
 */

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final ScoreRepository scoreRepository;
    private final StuRankRepository stuRankRepository;

    @Autowired
    public ExamServiceImpl(ExamRepository examRepository, SubjectRepository subjectRepository, ScoreRepository scoreRepository, StuRankRepository stuRankRepository) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.scoreRepository = scoreRepository;
        this.stuRankRepository = stuRankRepository;
    }

    @Override
    @Transactional
    public Result getExamList() {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return Result.error("未登录");
        }

        List<Exam> examList = examRepository.findByBindClassOrderByExamIDDesc(currentUser.getBindClass());
        List<Subject> subjectList = subjectRepository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("subject_list", subjectList);
        response.put("exam_list", examList);

        return Result.success(response);
    }

    @Override
    @Transactional
    public Result getExamReport(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        if (id == null) {
            return Result.error("参数错误");
        }

        List<Score> scores = scoreRepository.findByExamIDAndStudentID(id, currentUser.getClassId());
        StuRank rank = stuRankRepository.findByExamIdAndStudentId(id, currentUser.getClassId());
        Exam exam = examRepository.findByExamIDAndBindClass(id, currentUser.getBindClass());

        List<ScoreReportVO> scoreReportVOS = new ArrayList<>();

        if ("1".equals(exam.getIsOpen())) {
            for (Score score : scores) {
                ScoreReportVO scoreReportVO = new ScoreReportVO();
                scoreReportVO.setId(score.getId());
                scoreReportVO.setExamID(score.getExamID());
                scoreReportVO.setScore(score.getScore());
                scoreReportVO.setSubjectName(score.getSubjectName());
                scoreReportVO.setStudentID(score.getStudentID());

                Subject subject = subjectRepository.findBySubjectProp(score.getSubjectName());
                if (subject != null) {
                    scoreReportVO.setSubjectCNName(subject.getSubjectName());
                }
                scoreReportVOS.add(scoreReportVO);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("examName", exam.getExamName());
        response.put("isOpen", exam.getIsOpen());
        response.put("subject_output", scoreReportVOS);
        response.put("class_rank", rank != null ? rank.getClassRank() : null);
        response.put("zhuanye_rank", rank != null ? rank.getZhuanyeRank() : null);
        response.put("zonghe", rank != null ? rank.getZonghe() : null);

        return Result.success(response);
    }
}
