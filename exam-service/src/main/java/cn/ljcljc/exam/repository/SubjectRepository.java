package cn.ljcljc.exam.repository;

import cn.ljcljc.exam.domain.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, String> {
    Subject findBySubjectProp(String subjectName);
}
