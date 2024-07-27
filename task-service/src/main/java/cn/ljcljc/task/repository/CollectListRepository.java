package cn.ljcljc.task.repository;

import cn.ljcljc.task.domain.entity.CollectList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectListRepository extends JpaRepository<CollectList, Integer> {
    List<CollectList> findByBindClassOrderByIdDesc(String bindClass);

    List<CollectList> findByBindClassAndClassIdOrderByIdDesc(String bindClass, String classId);

    Optional<CollectList> findByIdAndBindClass(Integer id, String classId);
}
