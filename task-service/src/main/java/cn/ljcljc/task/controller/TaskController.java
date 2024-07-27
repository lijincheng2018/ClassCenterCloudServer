package cn.ljcljc.task.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.task.service.TaskTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "任务管理", description = "用于处理任务相关操作")
@RestController
@RequestMapping("/api/task")
public class TaskController {

    private final TaskTableService taskService;

    @Autowired
    public TaskController(TaskTableService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "获取任务列表")
    @GetMapping
    public Result listTasks(@RequestParam(required = false) String method) {
        return taskService.listTasks(method);
    }
}
