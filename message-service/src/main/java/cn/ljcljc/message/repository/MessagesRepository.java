package cn.ljcljc.message.repository;

import cn.ljcljc.message.domain.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Integer> {
    @Query("SELECT m FROM Messages m WHERE FUNCTION('FIND_IN_SET', :uid, m.recipients) > 0 AND FUNCTION('FIND_IN_SET', :uid, m.readBy) = 0 AND m.bindClass = :currentClassID")
    List<Messages> findUnreadMessages(@Param("uid") String uid, @Param("currentClassID") String currentClassID);

    @Query(value = "SELECT * FROM messages WHERE id = :id AND FIND_IN_SET(:uid, recipients) AND bindClass = :bindClass", nativeQuery = true)
    Messages findMessageByIdAndRecipientAndBindClass(@Param("id") Integer id, @Param("uid") Integer uid, @Param("bindClass") String bindClass);

    @Query(value = "SELECT * FROM messages WHERE FIND_IN_SET(:uid, recipients) AND NOT FIND_IN_SET(:uid, read_by) AND bindClass = :bindClass", nativeQuery = true)
    List<Messages> findUnreadMessagesByRecipientAndBindClass(@Param("uid") Integer uid, @Param("bindClass") String bindClass);

    @Query(value = "SELECT * FROM messages WHERE FIND_IN_SET(:uid, recipients) AND bindClass = :bindClass ORDER BY id DESC", nativeQuery = true)
    List<Messages> findAllMessagesByRecipientAndBindClass(@Param("uid") Integer uid, @Param("bindClass") String bindClass);
}
