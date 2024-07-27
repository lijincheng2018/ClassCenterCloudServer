package cn.ljcljc.exam.repository;

import cn.ljcljc.exam.domain.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Integer> {
    List<Score> findByExamIDAndStudentID(Integer examID, String studentID);
}
