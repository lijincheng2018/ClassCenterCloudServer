package cn.ljcljc.user.repository;

import cn.ljcljc.user.domain.entity.ClassroomInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassroomInfoRepository extends JpaRepository<ClassroomInfo, Integer> {
    Optional<ClassroomInfo> findByClassroomId(String classroomId);
}
