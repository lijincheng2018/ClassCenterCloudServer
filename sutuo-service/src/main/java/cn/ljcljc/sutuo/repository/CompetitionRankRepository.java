package cn.ljcljc.sutuo.repository;

import cn.ljcljc.sutuo.domain.entity.CompetitionRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionRankRepository extends JpaRepository<CompetitionRank, Integer> {
}
