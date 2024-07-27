package cn.ljcljc.say.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.say.domain.dto.SayDTO;
import cn.ljcljc.say.domain.entity.Say;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.say.repository.SayRepository;
import cn.ljcljc.say.service.SayService;
import cn.ljcljc.common.utils.ClassCenterUtils;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author ljc
 * @since 2024-7-16
 */

@Service
public class SayServiceImpl implements SayService {
    private final SayRepository sayRepository;

    @Autowired
    public SayServiceImpl(SayRepository sayRepository) {
        this.sayRepository = sayRepository;
    }

    @Override
    public List<Say> getSay(String bindClassId, Integer banwei) {
        return sayRepository.findByBindClassAndBanweiOrderByIdAsc(bindClassId, banwei);
    }

    @Override
    public List<Say> getUnReadSay(String bindClassId, Integer banwei) {
        return sayRepository.findByBindClassAndBanweiAndIsRead(bindClassId, banwei, "0");
    }

    @Override
    public Result getContent(Integer id) {
        UserInfo currentUser = UserContext.getUser();
        if (id == null) {
            return Result.error("参数错误");
        }

        Say say = sayRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (say == null) {
            return Result.error("留言不存在");
        }

        if (!say.getClassId().equals(currentUser.getClassId())) {
            return Result.error("无权限查看此留言");
        }

        return Result.success(say);
    }

    @Override
    @Transactional
    public Result addNewSay(SayDTO sayDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (sayDTO.getTitle() == null || sayDTO.getContent() == null || sayDTO.getBanwei() == null || sayDTO.getShiming() == null) {
            return Result.error("参数错误");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Say newSay = new Say();
        newSay.setBindClass(currentUser.getBindClass());
        newSay.setBanwei(Integer.valueOf(sayDTO.getBanwei()));
        newSay.setTitle(sayDTO.getTitle());
        newSay.setContent(sayDTO.getContent());
        newSay.setShiming(sayDTO.getShiming());
        newSay.setClassId(currentUser.getClassId());
        newSay.setAuthor(currentUser.getName());
        newSay.setIsRead("0");
        newSay.setReply("");
        newSay.setTime(formattedDateTime);

        try {
            sayRepository.save(newSay);
            return Result.success(newSay);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result listSays() {
        UserInfo currentUser = UserContext.getUser();

        try {
            List<Say> says = sayRepository.findByClassIdAndBindClassOrderByIdDesc(currentUser.getClassId(), currentUser.getBindClass());
            return Result.success(says);
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public Result getContentBanwei(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (id == null) {
            return Result.error("参数错误");
        }

        Say say = sayRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (say == null) {
            return Result.error("留言不存在");
        }

        if (!say.getBanwei().equals(ClassCenterUtils.converZhiwuToBanwei(currentUser.getZhiwu()))) {
            return Result.error("无权限查看此留言");
        }

        if ("0".equals(say.getIsRead())) {
            say.setIsRead("1");
            sayRepository.save(say);
        }

        if ("2".equals(say.getShiming())) {
            say.setAuthor("匿名");
        }

        return Result.success(say);
    }

    @Override
    public Result listSaysBanwei() {
        UserInfo currentUser = UserContext.getUser();

        Integer banwei = ClassCenterUtils.converZhiwuToBanwei(currentUser.getZhiwu());

        try {
            List<Say> says = sayRepository.findByBindClassAndBanweiOrderByIdAsc(currentUser.getBindClass(), banwei);
            for (Say say : says) {
                if ("2".equals(say.getShiming())) {
                    say.setAuthor("匿名");
                }
            }
            return Result.success(says);
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public Result postReply(Integer id, String reply) {
        UserInfo currentUser = UserContext.getUser();

        if (id == null || reply == null || reply.isEmpty()) {
            return Result.error("回复不可为空");
        }

        Say say = sayRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (say == null) {
            return Result.error("留言不存在");
        }

        if (!say.getBanwei().equals(ClassCenterUtils.converZhiwuToBanwei(currentUser.getZhiwu()))) {
            return Result.error("无权限回复此留言");
        }

        if (say.getReply() != null && !say.getReply().isEmpty()) {
            return Result.error("该留言不可重复回复");
        }

        say.setReply(reply);
        try {
            sayRepository.save(say);
            return Result.success();
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }
}
