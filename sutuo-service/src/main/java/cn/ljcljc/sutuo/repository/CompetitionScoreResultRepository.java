package cn.ljcljc.sutuo.repository;

import cn.ljcljc.sutuo.domain.entity.CompetitionScoreResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionScoreResultRepository extends JpaRepository<CompetitionScoreResult, String> {
    CompetitionScoreResult findByClassId(String classId);

    List<CompetitionScoreResult> findByCompetitionYearAndBindClass(Integer year, String bindClass);
}
