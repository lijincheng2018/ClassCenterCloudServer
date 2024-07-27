package cn.ljcljc.message.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.message.domain.entity.Messages;
import cn.ljcljc.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "消息接口", description = "用于处理消息相关操作")
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messagesService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messagesService = messageService;
    }

    @Operation(summary = "获取消息内容")
    @GetMapping("/{id}")
    public Result getContent(@PathVariable Integer id) {
        return messagesService.getContent(id);
    }

    @Operation(summary = "获取未读消息数量")
    @GetMapping("/unreadnum")
    public Result getUnreadNum() {
        return messagesService.getUnreadNum();
    }

    @Operation(summary = "获取消息列表")
    @GetMapping
    public Result listMessages() {
        return messagesService.listMessages();
    }

    @Operation(summary = "创建消息-微服务远程调用接口")
    @PostMapping("/feign/createMessage")
    public Boolean createMessage(@RequestParam String title, @RequestParam String content, @RequestParam String recipients) {
        return messagesService.createMessage(title, content, recipients);
    }

    @Operation(summary = "获取消息列表-微服务远程调用接口")
    @GetMapping("/feign/getUnreadMessageList")
    public List<Messages> listFeignMessages(@RequestParam Integer uid, @RequestParam String bindClass) {
        return messagesService.listFeignUnreadMessages(uid, bindClass);
    }
}
