package cn.ljcljc.task.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.task.domain.dto.RegisterDTO;
import cn.ljcljc.task.domain.vo.UserCenterTaskFinishRateVO;
import cn.ljcljc.task.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "登记表接口", description = "用于对登记表进行操作")
@RestController
@RequestMapping("/api/register")
public class RegisterController {
    private final RegisterService registerService;

    @Autowired
    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @Operation(summary = "用户获取登记表内容")
    @GetMapping("/{id}")
    public Result getRegisterDetail(@PathVariable Integer id) {
        return registerService.getRegisterDetail(id);
    }

    @Operation(summary = "用户完成登记表")
    @PostMapping("/{id}")
    public Result setRegister(@PathVariable Integer id) {
        return registerService.setRegister(id);
    }

    @Operation(summary = "管理员获取登记列表")
    @GetMapping("/admin")
    public Result getRegisterList() {
        return registerService.getRegisterAdminList();
    }

    @Operation(summary = "管理员获取登记表详细信息")
    @GetMapping("/admin/{id}")
    public Result getRegisterInfo(@PathVariable Integer id) {
        return registerService.getRegisterInfo(id);
    }

    @Operation(summary = "管理员设置用户完成情况")
    @PostMapping("/admin/{id}")
    public Result setSingleRegister(@PathVariable Integer id, @RequestParam Integer pid, @RequestParam String mode) {
        return registerService.setSingleRegister(id, pid, mode);
    }

    @Operation(summary = "管理员创建登记表")
    @PostMapping("/admin")
    public Result createRegister(@RequestBody RegisterDTO registerDTO) {
        return registerService.createRegister(registerDTO);
    }

    @Operation(summary = "管理员编辑登记表")
    @PutMapping("/admin/{id}")
    public Result editRegister(@PathVariable Integer id, @RequestBody RegisterDTO registerDTO) {
        return registerService.editRegister(id, registerDTO);
    }

    @Operation(summary = "管理员删除登记表表")
    @DeleteMapping("/admin/{id}")
    public Result deleteRegister(@PathVariable Integer id) {
        return registerService.deleteRegister(id);
    }

    @Operation(summary = "获取登记表完成率-微服务远程调用接口")
    @GetMapping("/feign/getRegisterFinishRate")
    public List<UserCenterTaskFinishRateVO> getRegisterFinishRate(@RequestParam String usergroup, @RequestParam String classid, @RequestParam String bindClass) {
        return registerService.getRegisterFinishRate(usergroup, classid, bindClass);
    }
}
