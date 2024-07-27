package cn.ljcljc.document.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.dto.DocDTO;
import cn.ljcljc.document.domain.entity.Doc;
import cn.ljcljc.document.domain.vo.DocVO;
import cn.ljcljc.document.repository.DocRepository;
import cn.ljcljc.document.service.DocService;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ljc
 * @since 2024-7-17
 */
@Service
public class DocServiceImpl implements DocService {

    private final DocRepository docRepository;

    @Autowired
    public DocServiceImpl(DocRepository docRepository) {
        this.docRepository = docRepository;
    }

    @Override
    @Transactional
    public Result addNewDoc(DocDTO docDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (docDTO.getTitle() == null || docDTO.getDuration() == null || docDTO.getRegion() == null || docDTO.getLeixing() == null) {
            return Result.error("参数错误");
        }

        Doc newDoc = new Doc();
        newDoc.setAuthor(currentUser.getName());
        newDoc.setClassId(currentUser.getClassId());
        newDoc.setRTime(docDTO.getDuration());
        newDoc.setDengji(docDTO.getLeixing());
        newDoc.setPlace(docDTO.getRegion());
        newDoc.setTime(docDTO.getTime());
        newDoc.setTitle(docDTO.getTitle());

        try {
            docRepository.save(newDoc);

            DocVO newDocVO = new DocVO();
            newDocVO.setUnid(newDoc.getId());
            newDocVO.setClassid(currentUser.getClassId());
            newDocVO.setPoster(currentUser.getName());
            newDocVO.setLeixing(docDTO.getLeixing());
            newDocVO.setRegion(docDTO.getRegion());
            newDocVO.setDuration(docDTO.getDuration());
            newDocVO.setTime(docDTO.getTime());
            newDocVO.setTitle(docDTO.getTitle());

            return Result.success(newDocVO);
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public Result editDoc(Integer id, DocDTO docDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (docDTO.getTitle() == null || docDTO.getDuration() == null || docDTO.getRegion() == null || docDTO.getLeixing() == null) {
            return Result.error("参数错误");
        }

        Doc newDoc = new Doc();
        newDoc.setId(id);
        newDoc.setAuthor(currentUser.getName());
        newDoc.setClassId(currentUser.getClassId());
        newDoc.setRTime(docDTO.getDuration());
        newDoc.setDengji(docDTO.getLeixing());
        newDoc.setPlace(docDTO.getRegion());
        newDoc.setTime(docDTO.getTime());
        newDoc.setTitle(docDTO.getTitle());

        try {
            docRepository.save(newDoc);
            return Result.success();
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public Result deleteDoc(Integer id) {
        if (id == null) {
            return Result.error("参数错误");
        }
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        try {
            Doc doc = docRepository.findByIdAndClassId(id, currentUser.getClassId());
            if (doc == null) {
                return Result.error("记录不存在或无权限删除");
            }
            docRepository.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    public Result getAllDocs() {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("用户不存在");
        }
        try {
            List<Doc> docs = docRepository.findByClassId(currentUser.getClassId());

            AtomicReference<Integer> id = new AtomicReference<>(1);

            List<DocVO> docVOS = docs.stream().map(doc -> {
                DocVO vo = new DocVO();
                vo.setId(id.getAndSet(id.get() + 1));
                vo.setUnid(doc.getId());
                vo.setTitle(doc.getTitle());
                vo.setTime(doc.getTime());
                vo.setPoster(doc.getAuthor());
                vo.setLeixing(doc.getDengji());
                vo.setClassid(doc.getClassId());
                vo.setRegion(doc.getPlace());
                vo.setDuration(doc.getRTime());
                return vo;
            }).toList();

            return Result.success(docVOS);
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }
}
