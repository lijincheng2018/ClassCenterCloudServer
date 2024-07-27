package cn.ljcljc.sutuo.repository;

import cn.ljcljc.sutuo.domain.entity.CompetitionClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionClassRepository extends JpaRepository<CompetitionClass, Integer> {
}
