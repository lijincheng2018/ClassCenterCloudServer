package cn.ljcljc.classfee.repository;

import cn.ljcljc.classfee.domain.entity.SystemInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemInfoRepository extends JpaRepository<SystemInfo, String> {
    SystemInfo findByTagAndBindClass(String tag, String bindClass);
}
