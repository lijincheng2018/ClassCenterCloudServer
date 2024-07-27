package cn.ljcljc.vote.repository;

import cn.ljcljc.vote.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer> {
    List<Vote> findByBindClassOrderByIdDesc(String bindClass);
    Vote findByIdAndBindClass(Integer id, String bindClass);
}
