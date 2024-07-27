package cn.ljcljc.document.repository;

import cn.ljcljc.document.domain.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Clazz, Integer> {
    List<Clazz> findByBindClassOrderByIdAsc(String bindClass);
}
