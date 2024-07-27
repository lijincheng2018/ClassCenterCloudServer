package cn.ljcljc.memorandum.repository;

import cn.ljcljc.memorandum.domain.entity.Memorandum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemorandumRepository extends JpaRepository<Memorandum, Integer> {
    List<Memorandum> findByClassIdOrderByIdDesc(String classId);

    Memorandum findByIdAndClassId(Integer id, String classId);
}
