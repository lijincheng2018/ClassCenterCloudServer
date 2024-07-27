package cn.ljcljc.classfee.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.classfee.domain.dto.QueueDTO;
import cn.ljcljc.classfee.domain.entity.Queue;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QueueService {
    List<Queue> getAllQueue(String bindClassId);

    List<Queue> getUnShenheQueue(String bindClassId);

    Result dealQueueItem(Integer id, QueueDTO queueDTO);

    Result getQueueDetail(Integer id);

    Result getQueueList();

    Result setNewQueueItem(String title, String money, String yt, String xiaofeiTime, MultipartFile file1, MultipartFile file2);
}
