package cn.ljcljc.say.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.say.domain.dto.SayDTO;
import cn.ljcljc.say.domain.entity.Say;
import cn.ljcljc.say.service.SayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "留言管理", description = "用于处理留言相关操作")
@RestController
@RequestMapping("/api/say")
public class SayController {
    private final SayService sayService;

    @Autowired
    public SayController(SayService sayService) {
        this.sayService = sayService;
    }

    @Operation(summary = "班委获取留言列表")
    @GetMapping("/admin")
    public Result listSaysAdmin() {
        return sayService.listSaysBanwei();
    }

    @Operation(summary = "班委回复留言")
    @PostMapping("/admin/{id}")
    public Result postReply(@PathVariable Integer id, @RequestParam String reply) {
        return sayService.postReply(id, reply);
    }

    @Operation(summary = "班委获取留言内容")
    @GetMapping("/admin/{id}")
    public Result getContentAdmin(@PathVariable Integer id) {
        return sayService.getContentBanwei(id);
    }

    @Operation(summary = "获取留言内容")
    @GetMapping("/{id}")
    public Result getContent(@PathVariable Integer id) {
        return sayService.getContent(id);
    }

    @Operation(summary = "新增留言")
    @PostMapping
    public Result createSay(@RequestBody SayDTO sayDTO) {
        return sayService.addNewSay(sayDTO);
    }

    @Operation(summary = "获取留言列表")
    @GetMapping
    public Result listSays() {
        return sayService.listSays();
    }

    @Operation(summary = "获取留言列表-微服务远程调用接口")
    @GetMapping("/feign/getSay")
    public List<Say> getSay(@RequestParam String bindClass, @RequestParam Integer banwei) {
        return sayService.getSay(bindClass, banwei);
    }

    @Operation(summary = "获取未读留言列表-微服务远程调用接口")
    @GetMapping("/feign/getUnReadSay")
    public List<Say> getUnReadSay(@RequestParam String bindClass, @RequestParam Integer banwei) {
        return sayService.getUnReadSay(bindClass, banwei);
    }
}
