package cn.ljcljc.career.service;

import cn.ljcljc.common.domain.Result;

public interface CareerService {
    Result getReportDetail(Integer reportId);
    Result getCareerPlanningList();
}
