package cn.ljcljc.career.service.Impl;


import cn.ljcljc.career.domain.entity.CareerPlanningList;
import cn.ljcljc.career.domain.entity.CareerReport;
import cn.ljcljc.career.repository.CareerPlanningListRepository;
import cn.ljcljc.career.repository.CareerReportRepository;
import cn.ljcljc.career.service.CareerService;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CareerServiceImpl implements CareerService {
    private final CareerReportRepository careerReportRepository;

    private final CareerPlanningListRepository careerPlanningListRepository;

    @Autowired
    public CareerServiceImpl(CareerReportRepository careerReportRepository, CareerPlanningListRepository careerPlanningListRepository) {
        this.careerReportRepository = careerReportRepository;
        this.careerPlanningListRepository = careerPlanningListRepository;
    }

    public Result getReportDetail(Integer reportId) {
        UserInfo currentUser = UserContext.getUser();

        Optional<CareerReport> report = careerReportRepository.findById(reportId);
        if (report.isEmpty()) {
            return Result.error("报告不存在");
        }
        if (!report.get().getClassid().equals(currentUser.getClassId())) {
            return Result.error("无权限查看");
        }

        return Result.success(report.get().getResult());
    }

    public Result getCareerPlanningList() {
        UserInfo currentUser = UserContext.getUser();

        List<CareerPlanningList> planningList = careerPlanningListRepository.findByClassidOrderByIdDesc(currentUser.getClassId());

        List<Map<String, Object>> output = new ArrayList<>();
        int a_id = 0;

        for (CareerPlanningList item : planningList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", a_id++);
            map.put("unid", item.getId());
            map.put("title", item.getTitle());
            map.put("reportId", item.getReportId());
            map.put("time", item.getTime());
            output.add(map);
        }

        return Result.success(output);
    }
}
