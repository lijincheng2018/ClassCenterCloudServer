package cn.ljcljc.sutuo.repository;

import cn.ljcljc.sutuo.domain.entity.CompetitionRecords;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompetitionRecordsRepository extends JpaRepository<CompetitionRecords, Integer> {
    List<CompetitionRecords> findByClassIdAndCompetitionYearAndBindClass(String classId, Integer competitionYear, String bindClass, Sort sort);

    Optional<CompetitionRecords> findByIdAndDeclarer(Integer id, String classId);

    List<CompetitionRecords> findByCompetitionYearAndBindClass(Integer year, String bindClass, Sort and);

    Optional<CompetitionRecords> findByIdAndBindClass(Integer id, String bindClass);
}
