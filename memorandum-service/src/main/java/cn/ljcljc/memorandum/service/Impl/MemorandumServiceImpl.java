package cn.ljcljc.memorandum.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.memorandum.domain.entity.Memorandum;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.memorandum.repository.MemorandumRepository;
import cn.ljcljc.memorandum.service.MemorandumService;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author ljc
 * @since 2024-7-18
 */

@Service
public class MemorandumServiceImpl implements MemorandumService {

    private final MemorandumRepository memorandumRepository;

    @Autowired
    public MemorandumServiceImpl(MemorandumRepository memorandumRepository) {
        this.memorandumRepository = memorandumRepository;
    }

    @Override
    @Transactional
    public Result addMemorandum(String content) {
        UserInfo currentUser = UserContext.getUser();

        if (content == null || content.isEmpty()) {
            return Result.error("备忘录内容不可为空");
        }

        Memorandum memorandum = new Memorandum();
        memorandum.setTitle(content);
        memorandum.setClassId(currentUser.getClassId());

        memorandumRepository.save(memorandum);

        return Result.success(Map.of("addid", memorandum.getId()));
    }

    @Override
    @Transactional
    public Result deleteMemorandum(Integer id) {
        UserInfo currentUser = UserContext.getUser();


        if (id == null) {
            return Result.error("参数不完整");
        }

        Memorandum memorandum = memorandumRepository.findByIdAndClassId(id, currentUser.getClassId());
        if (memorandum == null) {
            return Result.error("备忘录不存在或无权限删除");
        }

        memorandumRepository.delete(memorandum);

        return Result.success();
    }

    @Override
    @Transactional(readOnly = true)
    public Result listMemorandums() {
        UserInfo currentUser = UserContext.getUser();

        List<Memorandum> memorandums = memorandumRepository.findByClassIdOrderByIdDesc(currentUser.getClassId());
        return Result.success(memorandums);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Memorandum> listFeignMemorandums(String classId) {
        return memorandumRepository.findByClassIdOrderByIdDesc(classId);
    }
}
