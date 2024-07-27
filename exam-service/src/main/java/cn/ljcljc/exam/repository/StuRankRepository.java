package cn.ljcljc.exam.repository;

import cn.ljcljc.exam.domain.entity.StuRank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StuRankRepository extends JpaRepository<StuRank, Integer> {
    StuRank findByExamIdAndStudentId(Integer examId, String studentId);
}
