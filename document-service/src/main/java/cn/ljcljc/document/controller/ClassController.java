package cn.ljcljc.document.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.dto.ClassDTO;
import cn.ljcljc.document.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "班级接口", description = "用于处理班级荣誉和获取学生列表相关操作")
@RestController
@RequestMapping("/api/class")
public class ClassController {
    private final ClassService classService;

    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @Operation(summary = "新增班级荣誉")
    @PostMapping
    public Result createClass(@RequestBody ClassDTO classDTO) {
        return classService.addNewClass(classDTO.getTitle(), classDTO.getTime(), classDTO.getLeixing(), classDTO.getRank(), classDTO.getPeople());
    }

    @Operation(summary = "修改班级荣誉")
    @PutMapping("/{id}")
    public Result updateClass(@PathVariable Integer id, @RequestBody ClassDTO classDTO) {
        return classService.editClass(id, classDTO.getTitle(), classDTO.getTime(), classDTO.getLeixing(), classDTO.getRank(), classDTO.getPeople());
    }

    @Operation(summary = "删除班级荣誉")
    @DeleteMapping("/{id}")
    public Result deleteClass(@PathVariable Integer id) {
        return classService.deleteClass(id);
    }

    @Operation(summary = "获取班级荣誉")
    @GetMapping
    public Result getAllClasses() {
        return classService.getAllClasses();
    }

}
