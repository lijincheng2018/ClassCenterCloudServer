package cn.ljcljc.task.repository;

import cn.ljcljc.task.domain.entity.CollectData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectDataRepository extends JpaRepository<CollectData, Integer> {
    List<CollectData> findByCollectIdAndStatus(Integer collectId, String status);

    CollectData findByClassIdAndCollectId(String classId, Integer collectId);

    @Query("SELECT c FROM CollectData c WHERE c.collectId = :collectId ORDER BY CASE WHEN c.time = '' THEN 1 ELSE 0 END, c.time ASC")
    List<CollectData> findByCollectIdOrderByTimeAsc(@Param("collectId") Integer collectId);

    void deleteByCollectId(Integer collectId);

    @Modifying
    @Query("update CollectData c set c.status = '1', c.time = ?2, c.uploadFile = ?3, c.shenhe = '0' where c.classId = ?1 and c.collectId = ?4")
    void updateCollectDataForAdmin(String classid, Long time, String uploadFile, Integer collectId);

    @Modifying
    @Query("update CollectData c set c.status = '1', c.time = ?2, c.uploadFile = ?3, c.shenhe = '0' where c.uid = ?1 and c.collectId = ?4")
    void updateCollectDataForUser(Integer uid, Long time, String uploadFile, Integer collectId);

    List<CollectData> findTop10ByCollectIdAndStatusOrderByTimeAsc(Integer collectId, String pd);

    CollectData findByUidAndCollectId(Integer uid, Integer id);

    @Modifying
    @Query("update CollectData c set c.status = '0', c.time = '', c.uploadFile = '' where c.classId = ?1 and c.collectId = ?2")
    void clearCollectData(String classId, Integer collectId);

    @Modifying
    @Query("update CollectData c set c.shenhe = ?3 where c.classId = ?1 and c.collectId = ?2")
    void updateShenheStatus(String classId, Integer collectId, String shenhe);

    int countByStatusAndCollectId(String status, Integer collectId);
}
