package cn.ljcljc.message.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.message.domain.entity.Messages;
import cn.ljcljc.message.domain.entity.SysInfo;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.message.repository.MessagesRepository;
import cn.ljcljc.message.service.MessageService;
import cn.ljcljc.message.repository.SysInfoRepository;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author ljc
 * @since 2024-7-17
 */
@Service
public class MessageServiceImpl implements MessageService {

    private final MessagesRepository messagesRepository;
    private final SysInfoRepository sysInfoRepository;

    @Autowired
    public MessageServiceImpl(MessagesRepository messagesRepository, SysInfoRepository sysInfoRepository) {
        this.messagesRepository = messagesRepository;
        this.sysInfoRepository = sysInfoRepository;
    }

    @Override
    public Result getContent(Integer id) {
        UserInfo currentUser = UserContext.getUser();
        if (id == null) {
            return Result.error("参数错误");
        }

        Messages message = messagesRepository.findMessageByIdAndRecipientAndBindClass(id, currentUser.getUid(), currentUser.getBindClass());
        if (message == null) {
            return Result.error("消息不存在或无权限查看");
        }

        if (!message.getReadBy().contains(currentUser.getUid().toString())) {
            String readBy = message.getReadBy();
            readBy = readBy.isEmpty() ? currentUser.getUid().toString() : readBy + "," + currentUser.getUid();
            message.setReadBy(readBy);
            messagesRepository.save(message);
        }

        return Result.success(Map.of(
                "id", message.getId(),
                "title", message.getTitle(),
                "poster", message.getPoster(),
                "time", message.getTime(),
                "content", message.getContent()
        ));
    }

    @Override
    public Result getUnreadNum() {
        UserInfo currentUser = UserContext.getUser();

        List<Messages> unreadMessages = messagesRepository.findUnreadMessagesByRecipientAndBindClass(currentUser.getUid(), currentUser.getBindClass());
        int unreadNum = unreadMessages.size();

        SysInfo sysInfo = sysInfoRepository.findById(1).orElse(null);
        if (sysInfo == null) {
            return Result.error("系统信息获取失败");
        }

        return Result.success(Map.of(
                "usergroup", currentUser.getUserGroup(),
                "zhiwu", currentUser.getZhiwu(),
                "unreadnum", unreadNum,
                "warning_info", sysInfo.getMessage() != null ? sysInfo.getMessage() : "",
                "warning_type", sysInfo.getType() != null ? sysInfo.getType() : ""
        ));
    }

    @Override
    public Result listMessages() {
        UserInfo currentUser = UserContext.getUser();

        List<Messages> messages = messagesRepository.findAllMessagesByRecipientAndBindClass(currentUser.getUid(), currentUser.getBindClass());
        return Result.success(messages.stream().map(message -> Map.of(
                "id", message.getId(),
                "title", message.getTitle(),
                "poster", message.getPoster(),
                "time", message.getTime(),
                "isRead", message.getReadBy().contains(currentUser.getUid().toString())
        )).toList());
    }

    @Override
    @Transactional
    public Boolean createMessage(String title, String content, String recipients) {
        UserInfo currentUser = UserContext.getUser();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Messages message = new Messages();
        message.setTitle(title);
        message.setContent(content);
        message.setPoster("系统消息");
        message.setBindClass(currentUser.getBindClass());
        message.setRecipients(recipients);
        message.setReadBy("");
        message.setTime(formattedDateTime);
        messagesRepository.save(message);
        return true;
    }

    @Override
    public List<Messages> listFeignUnreadMessages(Integer uid, String bindClass) {
        return messagesRepository.findUnreadMessages(String.valueOf(uid), bindClass);
    }
}
