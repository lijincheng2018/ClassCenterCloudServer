package cn.ljcljc.exam.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.exam.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "成绩管理", description = "用于处理考试成绩相关操作")
@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;

    @Autowired
    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @Operation(summary = "获取考试列表")
    @GetMapping
    public Result getExamList() {
        return examService.getExamList();
    }

    @Operation(summary = "获取考试报告")
    @GetMapping("/{id}")
    public Result getExamReport(@PathVariable Integer id) {
        return examService.getExamReport(id);
    }
}
