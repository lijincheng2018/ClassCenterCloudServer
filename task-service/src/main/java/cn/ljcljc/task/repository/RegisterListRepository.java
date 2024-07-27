package cn.ljcljc.task.repository;

import cn.ljcljc.task.domain.entity.RegisterList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegisterListRepository extends JpaRepository<RegisterList, Integer> {
    List<RegisterList> findByBindClassOrderByIdDesc(String bindClass);

    List<RegisterList> findByBindClassAndClassIdOrderByIdDesc(String bindClass, String classId);

    Optional<RegisterList> findByIdAndBindClass(Integer id, String bindClass);
}
