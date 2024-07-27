package cn.ljcljc.task.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.task.domain.dto.CollectDTO;
import cn.ljcljc.task.domain.entity.CollectList;
import cn.ljcljc.task.domain.vo.UserCenterTaskFinishRateVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CollectService {
    List<CollectList> getCollectList(String bindClassId);

    List<CollectList> getCollectListByClassId(String bindClassId, String classId);

    List<UserCenterTaskFinishRateVO> getCollectFinishRate(String usergroup, String classid, String bindClass);

    Result getCollect(Integer id);

    Result getCollectAdminList();

    Result uploadCollect(MultipartFile file, String mode, String uClassId, Integer id);

    Result downloadCollect(Integer id);

    Result downloadSingleCollect(Integer id, Integer pid);

    Result clearCollect(Integer id, String cid);

    Result getCollectDetail(Integer id);

    Result createCollect(CollectDTO collectDTO);

    Result createZIP(Integer id);

    Result downloadZIP(String key);

    Result handleShenhe(String mode, Integer id, String classid);

    Result editCollect(Integer id, CollectDTO collectDTO);

    Result deleteCollect(Integer id);

}
