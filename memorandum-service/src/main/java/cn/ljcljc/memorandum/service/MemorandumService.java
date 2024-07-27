package cn.ljcljc.memorandum.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.memorandum.domain.entity.Memorandum;

import java.util.List;

public interface MemorandumService {
    Result addMemorandum(String content);
    Result deleteMemorandum(Integer id);
    Result listMemorandums();
    List<Memorandum> listFeignMemorandums(String classId);
}
