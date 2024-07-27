package cn.ljcljc.task.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.task.domain.dto.RegisterDTO;
import cn.ljcljc.task.domain.entity.RegisterList;
import cn.ljcljc.task.domain.vo.UserCenterTaskFinishRateVO;

import java.util.List;

public interface RegisterService {
    List<RegisterList> getRegisterList(String bindClassId);
    List<RegisterList> getRegisterListByClassId(String bindClassId, String classId);
    List<UserCenterTaskFinishRateVO> getRegisterFinishRate(String usergroup, String classid, String bindClass);
    Result getRegisterDetail(Integer id);
    Result getRegisterAdminList();
    Result getRegisterInfo(Integer id);
    Result setRegister(Integer id);
    Result setSingleRegister(Integer id, Integer pid, String mode);
    Result createRegister(RegisterDTO registerDTO);
    Result editRegister(Integer id, RegisterDTO registerDTO);
    Result deleteRegister(Integer id);
}
