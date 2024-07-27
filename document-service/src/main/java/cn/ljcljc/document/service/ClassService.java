package cn.ljcljc.document.service;

import cn.ljcljc.common.domain.Result;

public interface ClassService {
    Result addNewClass(String title, String time, String dengji, String mc, String people);
    Result editClass(Integer unid, String title, String time, String dengji, String mc, String people);
    Result deleteClass(Integer id);
    Result getAllClasses();
}
