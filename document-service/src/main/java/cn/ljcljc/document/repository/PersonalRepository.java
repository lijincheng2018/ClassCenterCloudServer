package cn.ljcljc.document.repository;

import cn.ljcljc.document.domain.entity.Personal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Integer> {
    List<Personal> findByClassIdOrderByIdAsc(String classId);
    Personal findByIdAndClassId(Integer id, String classId);
}
