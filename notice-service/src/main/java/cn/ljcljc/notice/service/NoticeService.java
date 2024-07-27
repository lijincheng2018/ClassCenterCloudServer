package cn.ljcljc.notice.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.notice.domain.dto.NoticeDTO;
import cn.ljcljc.notice.domain.entity.Notice;

import java.util.List;

public interface NoticeService {
    Result addNewNotice(NoticeDTO noticeDTO);
    Result editNotice(Integer id, NoticeDTO noticeDTO);
    Result deleteNotice(Integer id);
    Result listNotices();
    List<Notice> listFeignNotices(String classid);
}
