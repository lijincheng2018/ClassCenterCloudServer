package cn.ljcljc.career.repository;

import cn.ljcljc.career.domain.entity.CareerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerReportRepository extends JpaRepository<CareerReport, Integer> {

}