package cn.ljcljc.task.service;

import cn.ljcljc.common.domain.Result;

public interface TaskTableService {
    void addTaskTable(String taskName, Integer pid, String leixing, String time);
    void deleteTaskTableByPid(Integer id);
    void updateTaskTableName(Integer pid, String taskName);
    Result listTasks(String method);
}
