package cn.ljcljc.classfee.controller;

import cn.ljcljc.classfee.domain.dto.FeeDTO;
import cn.ljcljc.classfee.service.FeeService;
import cn.ljcljc.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "费用管理", description = "用于处理费用相关操作")
@RestController
@RequestMapping("/api/fees")
public class FeeController {
    private final FeeService feeService;

    @Autowired
    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @Operation(summary = "新增费用")
    @PostMapping
    public Result setNewFee(@RequestBody FeeDTO feeDTO) {
        return feeService.setNewFee(feeDTO);
    }

    @Operation(summary = "获取费用列表")
    @GetMapping
    public Result getFeeList() {
        return feeService.getFeeList();
    }
}
