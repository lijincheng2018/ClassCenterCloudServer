package cn.ljcljc.document.service.Impl;

import cn.ljcljc.api.client.UserClient;
import cn.ljcljc.api.dto.UserDTO;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.document.domain.entity.Clazz;
import cn.ljcljc.document.domain.vo.ClassVO;
import cn.ljcljc.document.repository.ClassRepository;
import cn.ljcljc.document.service.ClassService;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author ljc
 * @since 2024-7-17
 */

@Service
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final UserClient userClient;

    @Override
    @Transactional
    public Result addNewClass(String title, String time, String dengji, String mc, String people) {
        UserInfo currentUser = UserContext.getUser();
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        if (title.isEmpty() || time.isEmpty() || dengji.isEmpty() || mc.isEmpty() || people.isEmpty()) {
            return Result.error("参数错误");
        }

        String usergroup = currentUser.getUserGroup();
        if (!"1".equals(usergroup) && !"2".equals(usergroup)) {
            return Result.error("没有权限");
        }

        String BindClassId = currentUser.getBindClass();
        String name = currentUser.getName();
        List<Integer> peoples = Arrays.stream(people.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        int peopleNum = peoples.size();

        Clazz newClass = new Clazz();
        newClass.setBindClass(BindClassId);
        newClass.setTitle(title);
        newClass.setDengji(dengji);
        newClass.setMc(mc);
        newClass.setTime(time);
        newClass.setPeople(people);
        newClass.setPeopleNum(peopleNum);
        newClass.setAuthor(name);

        classRepository.save(newClass);

        ClassVO newClassVO = new ClassVO();
        newClassVO.setUnid(newClass.getId());
        newClassVO.setTitle(newClass.getTitle());
        newClassVO.setTime(newClass.getTime());
        newClassVO.setRank(newClass.getMc());
        newClassVO.setPoster(newClass.getAuthor());
        newClassVO.setLeixing(newClass.getDengji());
        newClassVO.setClassid(BindClassId);
        newClassVO.setPeoples(peoples);
        newClassVO.setPeopleNum(peopleNum);

        return Result.success(newClassVO);
    }

    @Override
    @Transactional
    public Result editClass(Integer unid, String title, String time, String dengji, String mc, String people) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        if (unid == null || title.isEmpty() || time.isEmpty() || dengji.isEmpty() || mc.isEmpty()) {
            return Result.error("参数错误");
        }

        String usergroup = currentUser.getUserGroup();
        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(usergroup) && !Constant.PERMISSION_GROUP_ADMIN.equals(usergroup)) {
            return Result.error("没有权限");
        }

        String BindClassId = currentUser.getBindClass();
        String name = currentUser.getName();
        List<String> peoples = Arrays.asList(people.split(","));
        int peopleNum = peoples.size();

        Optional<Clazz> existingClassOptional = classRepository.findById(unid);
        if (existingClassOptional.isEmpty() || !BindClassId.equals(existingClassOptional.get().getBindClass())) {
            return Result.error("记录不存在");
        }

        Clazz existingClass = existingClassOptional.get();
        existingClass.setTitle(title);
        existingClass.setMc(mc);
        existingClass.setDengji(dengji);
        existingClass.setTime(time);
        existingClass.setPeople(people);
        existingClass.setPeopleNum(peopleNum);
        existingClass.setAuthor(name);

        classRepository.save(existingClass);

        return Result.success();
    }

    @Override
    @Transactional
    public Result deleteClass(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        if (id == null) {
            return Result.error("参数错误");
        }

        String usergroup = currentUser.getUserGroup();
        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(usergroup) && !Constant.PERMISSION_GROUP_ADMIN.equals(usergroup)) {
            return Result.error("没有权限");
        }

        String BindClassId = currentUser.getBindClass();
        Optional<Clazz> existingClassOptional = classRepository.findById(id);
        if (existingClassOptional.isEmpty() || !BindClassId.equals(existingClassOptional.get().getBindClass())) {
            return Result.error("记录不存在");
        }

        classRepository.delete(existingClassOptional.get());

        return Result.success();
    }

    @Override
    public Result getAllClasses() {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        String BindClassId = currentUser.getBindClass();
        List<Clazz> classes = classRepository.findByBindClassOrderByIdAsc(BindClassId);

        AtomicReference<Integer> id = new AtomicReference<>(1);

        List<ClassVO> classVOs = classes.stream().map(clazz -> {
            ClassVO vo = new ClassVO();
            vo.setId(id.getAndSet(id.get() + 1));
            vo.setUnid(clazz.getId());
            vo.setTitle(clazz.getTitle());
            vo.setTime(clazz.getTime());
            vo.setRank(clazz.getMc());
            vo.setPoster(clazz.getAuthor());
            vo.setLeixing(clazz.getDengji());
            vo.setClassid(BindClassId);
            vo.setPeoples(Arrays.stream(clazz.getPeople().split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList()));
            vo.setPeopleNum(clazz.getPeopleNum());
            return vo;
        }).toList();

        List<UserDTO> users = userClient.getUserList(BindClassId);

        Map<String, Object> response = new HashMap<>();
        response.put("result", classVOs);
        response.put("people_list", users);
        response.put("usergroup", currentUser.getUserGroup());

        return Result.success(response);
    }
}
