package cn.ljcljc.notice.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.notice.domain.dto.NoticeDTO;
import cn.ljcljc.notice.domain.entity.Notice;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.notice.domain.vo.NoticeAdminVO;
import cn.ljcljc.notice.service.NoticeService;
import cn.ljcljc.notice.repository.NoticeRepository;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ljc
 * @since 2024-7-17
 */

@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    @Transactional
    public Result addNewNotice(NoticeDTO noticeDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null || (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup()))) {
            return Result.error("没有权限");
        }

        if (noticeDTO.getTitle() == null || noticeDTO.getContent() == null) {
            return Result.error("参数错误");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Notice newNotice = new Notice();
        newNotice.setBindClass(currentUser.getBindClass());
        newNotice.setTitle(noticeDTO.getTitle());
        newNotice.setContent(noticeDTO.getContent());
        newNotice.setAuthor(currentUser.getName());
        newNotice.setClassid(currentUser.getClassId());
        newNotice.setTime(formattedDateTime);

        try {
            noticeRepository.save(newNotice);

            NoticeAdminVO noticeAdminVO = new NoticeAdminVO();
            noticeAdminVO.setUnid(newNotice.getId());
            noticeAdminVO.setTitle(newNotice.getTitle());
            noticeAdminVO.setContent(newNotice.getContent());
            noticeAdminVO.setPoster(newNotice.getAuthor());
            noticeAdminVO.setTime(newNotice.getTime());
            noticeAdminVO.setQx(newNotice.getClassid().equals(currentUser.getClassId()));

            return Result.success(noticeAdminVO);
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public Result editNotice(Integer id, NoticeDTO noticeDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("没有权限");
        }

        if (id == null || noticeDTO.getTitle() == null || noticeDTO.getContent() == null) {
            return Result.error("参数错误");
        }

        Notice existingNotice = noticeRepository.findByIdAndClassidAndBindClass(id, currentUser.getClassId(), currentUser.getBindClass());
        if (existingNotice == null) {
            return Result.error("记录不存在或无权限删除");
        }

        existingNotice.setTitle(noticeDTO.getTitle());
        existingNotice.setContent(noticeDTO.getContent());

        try {
            noticeRepository.save(existingNotice);
            return Result.success();
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public Result deleteNotice(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("没有权限");
        }

        if (id == null) {
            return Result.error("参数错误");
        }

        Notice existingNotice = noticeRepository.findByIdAndClassidAndBindClass(id, currentUser.getClassId(), currentUser.getBindClass());
        if (existingNotice == null) {
            return Result.error("记录不存在或无权限删除");
        }

        try {
            noticeRepository.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    public Result listNotices() {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error(10001, "没有权限", null);
        }

        try {
            List<Notice> notices = noticeRepository.findByBindClassOrderByIdDesc(currentUser.getBindClass());
            List<NoticeAdminVO> noticeAdminVOS = new ArrayList<>();

            int i = 1;

            for (Notice notice : notices) {
                NoticeAdminVO noticeAdminVO = new NoticeAdminVO();
                noticeAdminVO.setId(i++);
                noticeAdminVO.setUnid(notice.getId());
                noticeAdminVO.setTitle(notice.getTitle());
                noticeAdminVO.setContent(notice.getContent());
                noticeAdminVO.setPoster(notice.getAuthor());
                noticeAdminVO.setTime(notice.getTime());
                noticeAdminVO.setQx(notice.getClassid().equals(currentUser.getClassId()));

                noticeAdminVOS.add(noticeAdminVO);
            }


            return Result.success(noticeAdminVOS);
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    public List<Notice> listFeignNotices(String bindClass) {
        return noticeRepository.findAllByBindClassOrderByIdDesc(bindClass).orElse(null);
    }
}
