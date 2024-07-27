package cn.ljcljc.task.repository;

import cn.ljcljc.task.domain.entity.RegisterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisterDataRepository extends JpaRepository<RegisterData, Integer> {
    List<RegisterData> findByRegisterIdAndStatus(Integer registerId, String status);

    RegisterData findByClassIdAndRegisterId(String classId, Integer registerId);

    RegisterData findByUidAndRegisterId(Integer uid, Integer registerId);

    List<RegisterData> findByRegisterIdOrderByTimeAsc(Integer registerId);

    int countByRegisterIdAndStatus(Integer registerId, String status);

    void deleteByRegisterId(Integer registerId);
}
