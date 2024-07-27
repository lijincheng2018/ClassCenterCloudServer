package cn.ljcljc.sutuo.repository;

import cn.ljcljc.sutuo.domain.entity.CompetitionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionTypeRepository extends JpaRepository<CompetitionType, Integer> {
}
