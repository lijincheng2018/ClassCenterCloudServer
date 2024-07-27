package cn.ljcljc.document.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.dto.PersonalDTO;

public interface PersonalService {
    Result addNewPersonal(PersonalDTO personalDTO);
    Result editPersonal(Integer id, PersonalDTO personalDTO);
    Result deletePersonal(Integer id);
    Result listPersonals();
}
