package cn.ljcljc.document.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.dto.DocDTO;

public interface DocService {
    Result addNewDoc(DocDTO docDTO);

    Result editDoc(Integer id, DocDTO docDTO);

    Result deleteDoc(Integer id);

    Result getAllDocs();
}
