package cn.ljcljc.classfee.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.classfee.domain.dto.FeeDTO;

public interface FeeService {
    Result setNewFee(FeeDTO feeDTO);

    Result getFeeList();
}
