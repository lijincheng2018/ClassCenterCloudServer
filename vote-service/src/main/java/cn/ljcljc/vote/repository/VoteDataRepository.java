package cn.ljcljc.vote.repository;

import cn.ljcljc.vote.domain.entity.VoteData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteDataRepository extends JpaRepository<VoteData, Integer> {
    VoteData findByPidAndClassId(Integer pid, String classId);

    List<VoteData> findByPidAndTimeNot(Integer pid, String time);

    List<VoteData> findByPid(Integer pid);

    @Query(value = "SELECT * FROM VoteData v WHERE v.pid = :pid ORDER BY CASE WHEN v.time = '' THEN 1 ELSE 0 END ASC, v.time ASC", nativeQuery = true)
    List<VoteData> findVoteDataByPid(Integer pid);
}