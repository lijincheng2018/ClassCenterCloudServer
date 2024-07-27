package cn.ljcljc.sutuo.repository;

import cn.ljcljc.sutuo.domain.entity.CompetitionPersonalHonor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionPersonalHonorRepository extends JpaRepository<CompetitionPersonalHonor, Integer> {
}
