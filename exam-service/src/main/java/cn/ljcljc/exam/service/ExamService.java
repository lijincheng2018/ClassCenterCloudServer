package cn.ljcljc.exam.service;

import cn.ljcljc.common.domain.Result;

public interface ExamService {
    Result getExamList();

    Result getExamReport(Integer id);
}
