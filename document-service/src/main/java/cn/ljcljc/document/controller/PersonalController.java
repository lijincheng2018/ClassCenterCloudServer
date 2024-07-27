package cn.ljcljc.document.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.dto.PersonalDTO;
import cn.ljcljc.document.service.PersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "个人荣誉", description = "用于处理个人荣誉相关操作")
@RestController
@RequestMapping("/api/personal")
public class PersonalController {
    private final PersonalService personalService;

    @Autowired
    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @Operation(summary = "新增个人荣誉")
    @PostMapping
    public Result createPersonal(@RequestBody PersonalDTO personalDTO) {
        return personalService.addNewPersonal(personalDTO);
    }

    @Operation(summary = "编辑个人荣誉")
    @PutMapping("/{id}")
    public Result updatePersonal(@PathVariable Integer id, @RequestBody PersonalDTO personalDTO) {
        return personalService.editPersonal(id, personalDTO);
    }

    @Operation(summary = "删除个人荣誉")
    @DeleteMapping("/{id}")
    public Result deletePersonal(@PathVariable Integer id) {
        return personalService.deletePersonal(id);
    }

    @Operation(summary = "获取个人荣誉列表")
    @GetMapping
    public Result listPersonals() {
        return personalService.listPersonals();
    }
}
