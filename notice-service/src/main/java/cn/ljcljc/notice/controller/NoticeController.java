package cn.ljcljc.notice.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.notice.domain.dto.NoticeDTO;
import cn.ljcljc.notice.domain.entity.Notice;
import cn.ljcljc.notice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "公告管理", description = "用于处理公告相关操作")
@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeService noticeService;

    @Autowired
    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }


    @Operation(summary = "新增公告")
    @PostMapping
    public Result createNotice(@RequestBody NoticeDTO noticeDTO) {
        return noticeService.addNewNotice(noticeDTO);
    }

    @Operation(summary = "编辑公告")
    @PutMapping("/{id}")
    public Result updateNotice(@PathVariable Integer id, @RequestBody NoticeDTO noticeDTO) {
        return noticeService.editNotice(id, noticeDTO);
    }

    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result deleteNotice(@PathVariable Integer id) {
        return noticeService.deleteNotice(id);
    }

    @Operation(summary = "获取公告列表")
    @GetMapping
    public Result listNotices() {
        return noticeService.listNotices();
    }

    @Operation(summary = "获取公告列表")
    @GetMapping("/feign/getList")
    public List<Notice> listFeignNotices(@RequestParam String bindClass) {
        return noticeService.listFeignNotices(bindClass);
    }
}
