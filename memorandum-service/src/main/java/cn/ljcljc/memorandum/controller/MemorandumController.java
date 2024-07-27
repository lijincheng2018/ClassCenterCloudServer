package cn.ljcljc.memorandum.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.memorandum.domain.entity.Memorandum;
import cn.ljcljc.memorandum.service.MemorandumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "备忘录接口", description = "用于处理备忘录相关操作")
@RestController
@RequestMapping("/api/memorandum")
public class MemorandumController {
    private final MemorandumService memorandumService;

    @Autowired
    public MemorandumController(MemorandumService memorandumService) {
        this.memorandumService = memorandumService;
    }

    @Operation(summary = "新增备忘录")
    @PostMapping
    public Result addMemorandum(@RequestParam String content) {
        return memorandumService.addMemorandum(content);
    }

    @Operation(summary = "删除备忘录")
    @DeleteMapping("/{id}")
    public Result deleteMemorandum(@PathVariable Integer id) {
        return memorandumService.deleteMemorandum(id);
    }

    @Operation(summary = "获取备忘录列表")
    @GetMapping
    public Result listMemorandums() {
        return memorandumService.listMemorandums();
    }

    @Operation(summary = "获取备忘录列表-微服务远程调用接口")
    @GetMapping("/feign/getList")
    public List<Memorandum> listFeignMemorandums(@RequestParam String classid) {
        return memorandumService.listFeignMemorandums(classid);
    }
}
