package cn.ljcljc.document.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.dto.PersonalDTO;
import cn.ljcljc.document.domain.entity.Personal;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.document.domain.vo.PersonalVO;
import cn.ljcljc.document.repository.PersonalRepository;
import cn.ljcljc.document.service.PersonalService;
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
public class PersonalServiceImpl implements PersonalService {

    private final PersonalRepository personalRepository;

    @Autowired
    public PersonalServiceImpl(PersonalRepository personalRepository) {
        this.personalRepository = personalRepository;
    }

    @Override
    @Transactional
    public Result addNewPersonal(PersonalDTO personalDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (personalDTO.getTitle() == null || personalDTO.getLeixing() == null || personalDTO.getRank() == null || personalDTO.getTime() == null) {
            return Result.error("参数错误");
        }

        Personal newPersonal = new Personal();
        newPersonal.setAuthor(currentUser.getName());
        newPersonal.setClassId(currentUser.getClassId());
        newPersonal.setTitle(personalDTO.getTitle());
        newPersonal.setDengji(personalDTO.getLeixing());
        newPersonal.setMc(personalDTO.getRank());
        newPersonal.setTime(personalDTO.getTime());


        try {
            personalRepository.save(newPersonal);

            PersonalVO newPersonalVO = new PersonalVO();
            newPersonalVO.setUnid(newPersonal.getId());
            newPersonalVO.setClassid(currentUser.getClassId());
            newPersonalVO.setPoster(currentUser.getName());
            newPersonalVO.setLeixing(personalDTO.getLeixing());
            newPersonalVO.setRank(personalDTO.getRank());
            newPersonalVO.setTime(personalDTO.getTime());
            newPersonalVO.setTitle(personalDTO.getTitle());

            return Result.success(newPersonalVO);
        } catch (Exception e) {
            return Result.error(10001, e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public Result editPersonal(Integer id, PersonalDTO personalDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (personalDTO.getTitle() == null || personalDTO.getLeixing() == null || personalDTO.getRank() == null || personalDTO.getTime() == null) {
            return Result.error("参数错误");
        }

        Personal existingPersonal = personalRepository.findByIdAndClassId(id, currentUser.getClassId());
        if (existingPersonal == null) {
            return Result.error("记录不存在或无权限编辑");
        }

        existingPersonal.setTitle(personalDTO.getTitle());
        existingPersonal.setDengji(personalDTO.getLeixing());
        existingPersonal.setMc(personalDTO.getRank());
        existingPersonal.setTime(personalDTO.getTime());
        existingPersonal.setAuthor(currentUser.getName());

        try {
            personalRepository.save(existingPersonal);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result deletePersonal(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (id == null) {
            return Result.error("参数错误");
        }

        Personal existingPersonal = personalRepository.findByIdAndClassId(id, currentUser.getClassId());
        if (existingPersonal == null) {
            return Result.error("记录不存在或无权限删除");
        }

        try {
            personalRepository.deleteById(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result listPersonals() {
        UserInfo currentUser = UserContext.getUser();

        try {
            List<Personal> personals = personalRepository.findByClassIdOrderByIdAsc(currentUser.getClassId());
            AtomicReference<Integer> id = new AtomicReference<>(1);

            List<PersonalVO> personalVOS = personals.stream().map(personal -> {
                PersonalVO vo = new PersonalVO();
                vo.setId(id.getAndSet(id.get() + 1));
                vo.setUnid(personal.getId());
                vo.setTitle(personal.getTitle());
                vo.setTime(personal.getTime());
                vo.setRank(personal.getMc());
                vo.setClassid(personal.getClassId());
                vo.setPoster(personal.getAuthor());
                vo.setLeixing(personal.getDengji());
                return vo;
            }).toList();

            return Result.success(personalVOS);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
