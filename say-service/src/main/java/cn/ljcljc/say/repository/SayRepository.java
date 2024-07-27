package cn.ljcljc.say.repository;

import cn.ljcljc.say.domain.entity.Say;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SayRepository extends JpaRepository<Say, Integer> {
    List<Say> findByBindClassAndBanweiOrderByIdAsc(String bindClassId, Integer banwei);

    List<Say> findByBindClassAndBanweiAndIsRead(String bindClassId, Integer banwei, String isRead);

    List<Say> findByClassIdAndBindClassOrderByIdDesc(String classId, String bindClass);

    Say findByIdAndBindClass(Integer id, String bindClass);

}
