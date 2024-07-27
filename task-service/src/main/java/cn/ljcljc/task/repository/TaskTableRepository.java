package cn.ljcljc.task.repository;

import cn.ljcljc.task.domain.entity.TaskTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskTableRepository extends JpaRepository<TaskTable, Integer> {
    void deleteByPid(Integer pid);
    TaskTable findByPid(Integer pid);

    List<TaskTable> findAllByOrderByIdDesc();
}
