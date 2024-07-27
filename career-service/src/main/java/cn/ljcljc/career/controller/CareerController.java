package cn.ljcljc.career.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.career.service.CareerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "职业生涯规划接口", description = "用于处理职业生涯规划相关操作")
@RestController
@RequestMapping("/api/career")
public class CareerController {
    private final CareerService careerService;

    @Autowired
    public CareerController(CareerService careerService) {
        this.careerService = careerService;
    }

    @Operation(summary = "查询报告详情")
    @GetMapping("/{id}")
    public Result getDetail(@PathVariable Integer id) {
        return careerService.getReportDetail(id);
    }

    @Operation(summary = "查询报告列表")
    @GetMapping
    public Result list() {
        return careerService.getCareerPlanningList();
    }
}
