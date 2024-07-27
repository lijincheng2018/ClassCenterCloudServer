package cn.ljcljc.user.repository;

import cn.ljcljc.user.domain.entity.AllUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllUserRepository extends JpaRepository<AllUser, Integer> {
    AllUser findByClassIdAndPasswd(String username, String password);

    AllUser findByClassId(String classId);

    AllUser findByQqId(String code);

    AllUser findByWxId(String openid);
}
