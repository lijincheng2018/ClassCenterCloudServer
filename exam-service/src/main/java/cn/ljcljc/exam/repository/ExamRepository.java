package cn.ljcljc.exam.repository;

import cn.ljcljc.exam.domain.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer> {
    List<Exam> findByBindClassOrderByExamIDDesc(String bindClass);

    Exam findByExamIDAndBindClass(Integer examID, String bindClass);
}
