package cn.ljcljc.document.repository;

import cn.ljcljc.document.domain.entity.Doc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocRepository extends JpaRepository<Doc, Integer> {
    List<Doc> findByClassId(String classId);
    Doc findByIdAndClassId(Integer id, String classId);
}
