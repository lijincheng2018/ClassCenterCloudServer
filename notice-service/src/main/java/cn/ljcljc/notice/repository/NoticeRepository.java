package cn.ljcljc.notice.repository;

import cn.ljcljc.notice.domain.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {
    Optional<List<Notice>> findAllByBindClassOrderByIdDesc(String bindClass);

    List<Notice> findByBindClassOrderByIdDesc(String bindClass);

    Notice findByIdAndClassidAndBindClass(Integer id, String name, String bindClass);
}
