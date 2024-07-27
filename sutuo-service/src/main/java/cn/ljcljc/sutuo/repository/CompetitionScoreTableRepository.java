package cn.ljcljc.sutuo.repository;

import cn.ljcljc.sutuo.domain.entity.CompetitionScoreTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionScoreTableRepository extends JpaRepository<CompetitionScoreTable, Integer> {
    CompetitionScoreTable findByCmptTypeAndCmptLevelAndCmptClass(String cmptType, String cmptLevel, String cmptClass);

    CompetitionScoreTable findByCmptTypeAndCmptClass(String number, String compClass);
}
