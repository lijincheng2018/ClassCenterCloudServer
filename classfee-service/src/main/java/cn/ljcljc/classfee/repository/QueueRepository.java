package cn.ljcljc.classfee.repository;

import cn.ljcljc.classfee.domain.entity.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Integer> {
    Optional<List<Queue>> findByBindClass(String bindClassId);

    Optional<List<Queue>> findByMethodAndBindClass(String method, String bindClassId);

    Queue findByIdAndBindClass(Integer id, String bindClass);

    List<Queue> findByBindClassOrderByIdDesc(String bindClass);

    @Query(value = "INSERT INTO fee (bindClass, title, fee, after_f, method, author, time) VALUES (:bindClass, :title, :fee, :afterF, :method, :author, :time)", nativeQuery = true)
    void saveFee(@Param("bindClass") String bindClass, @Param("title") String title, @Param("fee") Double fee, @Param("afterF") Double afterF, @Param("method") String method, @Param("author") String author, @Param("time") String time);
}
