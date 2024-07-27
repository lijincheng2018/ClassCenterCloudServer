package cn.ljcljc.task.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.task.domain.dto.CollectDTO;
import cn.ljcljc.task.domain.vo.UserCenterTaskFinishRateVO;
import cn.ljcljc.task.service.CollectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "收集表管理", description = "用于处理收集表相关操作")
@RestController
@RequestMapping("/api/collect")
public class CollectController {

    private final CollectService collectService;

    @Autowired
    public CollectController(CollectService collectService) {
        this.collectService = collectService;
    }

    @Operation(summary = "用户获取收集表内容")
    @GetMapping("/{id}")
    public Result getCollect(@PathVariable Integer id) {
        return collectService.getCollect(id);
    }

    @Operation(summary = "管理员/用户上传文件")
    @PostMapping("/{id}")
    public Result uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam(value = "mode", required = false) String mode,
                             @RequestParam(value = "classid", required = false) String classId,
                             @PathVariable Integer id) {
        return collectService.uploadCollect(file, mode, classId, id);
    }

    @Operation(summary = "用户下载收集表")
    @GetMapping("/download/{id}")
    public Result downloadCollect(@PathVariable("id") Integer id) {
        return collectService.downloadCollect(id);
    }

    @Operation(summary = "管理员获取收集表详细信息")
    @GetMapping("/admin/{id}")
    public Result getCollectDetail(@PathVariable Integer id) {
        return collectService.getCollectDetail(id);
    }

    @Operation(summary = "管理员创建收集表")
    @PostMapping("/admin")
    public Result createCollect(@RequestBody CollectDTO collectDTO) {
        return collectService.createCollect(collectDTO);
    }

    @Operation(summary = "管理员获取收集表列表")
    @GetMapping("/admin")
    public Result getCollectAdminList() {
        return collectService.getCollectAdminList();
    }

    @Operation(summary = "管理员下载单用户收集表")
    @GetMapping("/admin/download/{id}")
    public Result downloadSingleCollect(@PathVariable("id") Integer id,
                                        @RequestParam("pid") Integer pid) {
        return collectService.downloadSingleCollect(id, pid);
    }

    @Operation(summary = "管理员编辑收集表")
    @PutMapping("/admin/{id}")
    public Result editCollect(@PathVariable Integer id, CollectDTO collectDTO) {
        return collectService.editCollect(id, collectDTO);
    }

    @Operation(summary = "管理员删除收集表")
    @DeleteMapping("/admin/{id}")
    public Result deleteCollect(@PathVariable Integer id) {
        return collectService.deleteCollect(id);
    }

    @Operation(summary = "管理员清除某个用户的收集表")
    @PostMapping("/admin/clear/{id}")
    public Result clearCollect(@PathVariable Integer id,
                               @RequestParam("classid") String cid) {
        return collectService.clearCollect(id, cid);
    }

    @Operation(summary = "管理员处理审核请求")
    @PostMapping("/admin/handleShenhe")
    public Result handleShenhe(@RequestParam("mode") String mode,
                               @RequestParam("id") Integer id,
                               @RequestParam("classid") String classid) {
        return collectService.handleShenhe(mode, id, classid);
    }

    @Operation(summary = "创建ZIP压缩包")
    @PostMapping("/admin/createZIP")
    public Result createZIP(@RequestParam("id") Integer id) {
        return collectService.createZIP(id);
    }

    @Operation(summary = "下载ZIP压缩包")
    @PostMapping("/admin/downloadZIP")
    public Result downloadZIP(@RequestParam("key") String fileKey) {
        return collectService.downloadZIP(fileKey);
    }

    @Operation(summary = "获取收集完成率-微服务远程调用接口")
    @GetMapping("/feign/getCollectFinishRate")
    public List<UserCenterTaskFinishRateVO> getCollectFinishRate(@RequestParam String usergroup, @RequestParam String classid, @RequestParam String bindClass) {
        return collectService.getCollectFinishRate(usergroup, classid, bindClass);
    }
}
