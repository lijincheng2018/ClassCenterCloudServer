package cn.ljcljc.classfee.controller;

import cn.ljcljc.classfee.domain.dto.QueueDTO;
import cn.ljcljc.classfee.domain.entity.Queue;
import cn.ljcljc.classfee.service.QueueService;
import cn.ljcljc.common.domain.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "报销接口", description = "用于申请、查看报销和处理报销队列相关操作")
@RestController
@RequestMapping("/api/queue")
public class QueueController {
    private final QueueService queueService;

    @Autowired
    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public Result getQueueDetail(@PathVariable Integer id) {
        return queueService.getQueueDetail(id);
    }


    @Operation(summary = "获取队列列表")
    @GetMapping
    public Result getQueueList() {
        return queueService.getQueueList();
    }

    @Operation(summary = "新增队列项")
    @PostMapping
    public Result setNewQueueItem(
            @RequestParam("title") String title,
            @RequestParam("money") String money,
            @RequestParam("yt") String yt,
            @RequestParam("xiaofei_time") String xiaofeiTime,
            @RequestParam("file1") MultipartFile file1,
            @RequestParam("file2") MultipartFile file2) {
        return queueService.setNewQueueItem(title, money, yt, xiaofeiTime, file1, file2);
    }


    @Operation(summary = "审核处理报销项")
    @PutMapping("/admin/{id}")
    public Result dealQueueItem(@PathVariable Integer id, @RequestBody QueueDTO queueDTO) {
        return queueService.dealQueueItem(id, queueDTO);
    }

    @Operation(summary = "获取队列列表-微服务远程调用接口")
    @GetMapping("/feign/getAllQueue")
    public List<Queue> getAllQueue(String bindClass) {
        return queueService.getAllQueue(bindClass);
    }

    @Operation(summary = "获取未审核队列列表-微服务远程调用接口")
    @GetMapping("/feign/getUnShenheQueue")
    public List<Queue> getUnShenheQueue(String bindClass) {
        return queueService.getUnShenheQueue(bindClass);
    }

}
