package cn.ljcljc.user.repository;

import cn.ljcljc.user.domain.entity.WeChatMPQrLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeChatMPQrLoginRepository extends JpaRepository<WeChatMPQrLogin, Integer> {
    WeChatMPQrLogin findByScene(String scene);
}
