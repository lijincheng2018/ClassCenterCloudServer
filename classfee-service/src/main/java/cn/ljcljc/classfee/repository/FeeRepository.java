package cn.ljcljc.classfee.repository;

import cn.ljcljc.classfee.domain.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Integer> {
    List<Fee> findByBindClassOrderByIdDesc(String bindClass);
}
