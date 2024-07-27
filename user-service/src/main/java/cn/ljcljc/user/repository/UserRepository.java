package cn.ljcljc.user.repository;

import cn.ljcljc.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByClassId(String classId);

    @Query("SELECT u FROM User u WHERE u.zzmm IN :zzmmList AND u.bindClass = :bindClass")
    Optional<List<User>> findAllByZzmmInAndBindClass(@Param("zzmmList") List<String> zzmmList, @Param("bindClass") String bindClass);

    Optional<List<User>> findAllByBindClass(String bindClass);

    List<User> findByBindClassOrderByIdAsc(String bindClass);

    List<User> findAllUsersByBindClass(String bindClass);

    User findByUidAndBindClass(Integer uid, String bindClass);

    List<User> findByBindClass(String bindClass);

    User findByZhiwuAndBindClass(String zhiwu, String bindClass);

    List<User> findByIdInAndBindClass(Collection<Integer> id, String bindClass);
}
