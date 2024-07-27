package cn.ljcljc.career.repository;

import cn.ljcljc.career.domain.entity.CareerPlanningList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerPlanningListRepository extends JpaRepository<CareerPlanningList, Integer> {
    List<CareerPlanningList> findByClassidOrderByIdDesc(String classid);
}