package cn.ljcljc.vote.service.Impl;

import cn.ljcljc.api.client.MessageClient;
import cn.ljcljc.api.client.UserClient;
import cn.ljcljc.api.dto.UserDTO;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.vote.domain.entity.Vote;
import cn.ljcljc.vote.domain.entity.VoteData;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.vote.domain.vo.VoteDetailVO;
import cn.ljcljc.vote.domain.vo.VoteListAdminVO;
import cn.ljcljc.vote.domain.vo.VoteListVO;
import cn.ljcljc.vote.repository.VoteDataRepository;
import cn.ljcljc.vote.repository.VoteRepository;
import cn.ljcljc.vote.service.VoteService;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ljc
 * @since 2024-7-17
 */

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final VoteDataRepository voteDataRepository;
    private final UserClient userClient;
    private final MessageClient messageClient;


    @Override
    @Transactional(readOnly = true)
    public Result listVotes() {
        UserInfo currentUser = UserContext.getUser();
        List<Vote> votes = voteRepository.findByBindClassOrderByIdDesc(currentUser.getBindClass());

        List<VoteListVO> output = new ArrayList<>();
        int total = 0;
        int finishNum = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Vote vote : votes) {
            String[] people = vote.getPeople().split(",");
            boolean isUserInVote = false;
            for (String person : people) {
                if (person.equals(currentUser.getUid().toString())) {
                    isUserInVote = true;
                    break;
                }
            }

            if (isUserInVote) {
                total++;
                VoteData voteData = voteDataRepository.findByPidAndClassId(vote.getId(), currentUser.getClassId());
                boolean isFinish = voteData != null && voteData.getTime() != null && !voteData.getTime().isEmpty();

                if (isFinish) {
                    finishNum++;
                }

                int status;
                LocalDateTime startTime = LocalDateTime.parse(vote.getStartTime(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(vote.getEndTime(), formatter);
                LocalDateTime now = LocalDateTime.now();

                if (now.isBefore(startTime)) {
                    status = 1;
                } else if (!now.isAfter(endTime)) {
                    status = 2;
                } else {
                    status = 3;
                }

                output.add(new VoteListVO(
                        total,
                        vote.getId(),
                        vote.getTitle(),
                        isFinish,
                        vote.getAuthor(),
                        vote.getTime(),
                        vote.getEndTime(),
                        status,
                        vote.getAnonymous()
                ));
            }
        }

        return Result.success(Map.of(
                "total", total,
                "finish", finishNum,
                "votelist", output
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Result getVoteList() {
        UserInfo currentUser = UserContext.getUser();

        List<Vote> votes;
        if (Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            votes = voteRepository.findByBindClassOrderByIdDesc(currentUser.getBindClass());
        } else {
            votes = voteRepository.findByBindClassOrderByIdDesc(currentUser.getClassId());
        }

        List<VoteListAdminVO> output = new ArrayList<>();

        for (Vote vote : votes) {
            int id = vote.getId();
            int countCntNum = voteDataRepository.findByPidAndTimeNot(id, "").size();
            output.add(new VoteListAdminVO(
                    vote.getId(),
                    vote.getTitle(),
                    vote.getTime(),
                    List.of(vote.getStartTime(), vote.getEndTime()),
                    vote.getAnonymous(),
                    vote.getAuthor(),
                    vote.getPeople().split(","),
                    vote.getPeopleNum(),
                    vote.getSelectPeople().split(","),
                    vote.getSelectNum(),
                    countCntNum
            ));
        }

        List<Map<String, Object>> peopleList = new ArrayList<>();
        List<UserDTO> users = userClient.getUserList(currentUser.getBindClass());
        for (UserDTO user : users) {
            peopleList.add(Map.of("id", user.getUid(), "name", user.getName()));
        }

        return Result.success(Map.of("people_list", peopleList, "result", output));
    }

    @Override
    @Transactional(readOnly = true)
    public Result getVoteDetail(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (id == null) {
            return Result.error("error");
        }

        Vote vote = voteRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (vote == null) {
            return Result.error("没有数据");
        }

        List<Map<String, Object>> peopleList = new ArrayList<>();
        String[] selectPeoples = vote.getSelectPeople().split(",");
        for (String selectPeople : selectPeoples) {
            UserDTO user = userClient.getUser(Integer.parseInt(selectPeople), currentUser.getBindClass());
            peopleList.add(Map.of("id", user.getUid(), "name", user.getName()));

        }

        VoteData voteData = voteDataRepository.findByPidAndClassId(id, currentUser.getClassId());
        if (voteData == null) {
            return Result.error("没有权限查看和提交此投票");
        }

        boolean ifFinish = voteData.getTime() != null && !voteData.getTime().isEmpty();
        List<String> selectPeoplesData = new ArrayList<>();
        if (ifFinish) {
            selectPeoplesData = Arrays.asList(voteData.getVoteTo().split(","));
        }

        int status;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(vote.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(vote.getEndTime(), formatter);
        if (now.isBefore(startTime)) {
            status = 1;
        } else if (!now.isAfter(endTime)) {
            status = 2;
        } else {
            status = 3;
        }

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("people_list", peopleList);
        responseMap.put("votedata", new VoteDetailVO(
                vote.getId(),
                vote.getTitle(),
                ifFinish,
                selectPeoplesData,
                vote.getSelectNum(),
                vote.getAnonymous(),
                vote.getAuthor(),
                status
        ));


        return Result.success(responseMap);
    }

    @Override
    @Transactional
    public Result createVote(String title, String people, String selectPeople, String startTime, String endTime, Boolean anonymous, Integer selectNum) {
        UserInfo currentUser = UserContext.getUser();


        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("没有权限");
        }

        if (title == null || title.isEmpty() || selectPeople == null || selectPeople.isEmpty() || people == null || people.isEmpty() || startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty() || selectNum == null) {
            return Result.error("参数错误");
        }

        String[] peopleArr = people.split(",");
        int peopleNum = peopleArr.length;

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Vote vote = new Vote();
        vote.setBindClass(currentUser.getBindClass());
        vote.setTitle(title);
        vote.setPeople(people);
        vote.setPeopleNum(peopleNum);
        vote.setSelectPeople(selectPeople);
        vote.setSelectNum(selectNum);
        vote.setAuthor(currentUser.getName());
        vote.setClassId(currentUser.getClassId());
        vote.setTime(time);
        vote.setStartTime(startTime);
        vote.setEndTime(endTime);
        vote.setAnonymous(anonymous);

        voteRepository.save(vote);

        // 创建消息
        String messageContent = "投票标题：" + title + "<br>投票ID：" + vote.getId() + "<br>开始投票的时间为：" + startTime + "<br>结束投票的时间为：" + endTime + "<br>请尽快完成投票~<br><a href=\"https://class.ljcljc.cn/user/vote/" + vote.getId() + "\">点击跳转到该投票</a>";
        messageClient.createMessage("【投票提醒】有新的投票啦！", messageContent, people);

        // 创建投票数据
        for (String person : peopleArr) {
            UserDTO user = userClient.getUser(Integer.parseInt(person), currentUser.getBindClass());

            String nowClassId = user.getClassId();
            VoteData voteData = new VoteData();
            voteData.setPid(vote.getId());
            voteData.setClassId(nowClassId);
            voteData.setVoteTo("");
            voteData.setTime("");
            voteDataRepository.save(voteData);
        }


        return Result.success(new VoteListAdminVO(
                vote.getId(),
                vote.getTitle(),
                vote.getTime(),
                List.of(vote.getStartTime(), vote.getEndTime()),
                vote.getAnonymous(),
                vote.getAuthor(),
                vote.getPeople().split(","),
                vote.getPeopleNum(),
                vote.getSelectPeople().split(","),
                vote.getSelectNum(),
                0
        ));
    }

    @Override
    @Transactional
    public Result editVote(Integer id, String title, String startTime, String endTime) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("没有权限");
        }

        if (id == null || title == null || title.isEmpty() || startTime == null || startTime.isEmpty() || endTime == null || endTime.isEmpty()) {
            return Result.error("参数错误");
        }

        Vote vote = voteRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (vote == null) {
            return Result.error("没有数据");
        }

        if (!vote.getClassId().equals(currentUser.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        vote.setTitle(title);
        vote.setStartTime(startTime);
        vote.setEndTime(endTime);

        voteRepository.save(vote);

        return Result.success();
    }

    @Override
    @Transactional
    public Result deleteVote(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null) {
            return Result.error("error");
        }

        Vote vote = voteRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (vote == null) {
            return Result.error("没有数据");
        }

        if (!vote.getClassId().equals(currentUser.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        voteRepository.delete(vote);
        voteDataRepository.deleteById(id);

        return Result.success();
    }

    @Override
    @Transactional
    public Result vote(Integer id, String selectPeople) {
        UserInfo currentUser = UserContext.getUser();

        if (id == null || selectPeople == null || selectPeople.isEmpty()) {
            return Result.error("error");
        }

        Vote vote = voteRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (vote == null) {
            return Result.error("没有数据");
        }

        VoteData voteData = voteDataRepository.findByPidAndClassId(id, currentUser.getClassId());
        if (voteData == null || voteData.getTime() != null && !voteData.getTime().isEmpty()) {
            return Result.error("已有投票记录，不可重复投票");
        }

        int selectNum = vote.getSelectNum();
        String[] selectedPeople = selectPeople.split(",");
        if (selectedPeople.length != selectNum) {
            return Result.error("请确认选择的人数是否正确");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(vote.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(vote.getEndTime(), formatter);
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            return Result.error("不在允许提交的时间范围内");
        }

        String time = now.format(formatter);
        voteData.setVoteTo(selectPeople);
        voteData.setTime(time);

        voteDataRepository.save(voteData);

        return Result.success();
    }

    @Override
    @Transactional(readOnly = true)
    public Result getVoteSummary(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup()) && !Constant.PERMISSION_GROUP_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null) {
            return Result.error("参数不完整");
        }

        Vote vote = voteRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (vote == null) {
            return Result.error("没有数据");
        }

        if (!vote.getClassId().equals(currentUser.getClassId()) && !Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        int countCntNum = voteDataRepository.findByPidAndTimeNot(id, "").size();

        List<Map<String, Object>> output = new ArrayList<>();
        List<VoteData> voteDataList = voteDataRepository.findVoteDataByPid(id);
        List<UserDTO> userDTOList = userClient.getUserList(currentUser.getBindClass());

        int rank = 1;
        for (VoteData voteData : voteDataList) {
            String peoClassid = voteData.getClassId();
            List<String> peoSelectPeoples = new ArrayList<>();
            if (!vote.getAnonymous()) {
                String voteTo = voteData.getVoteTo();
                peoSelectPeoples = Arrays.asList(voteTo.split(","));
            }

            for(UserDTO userDTO : userDTOList) {
                if (userDTO.getClassId().equals(peoClassid)) {
                    output.add(Map.of(
                            "id", userDTO.getUid(),
                            "name", userDTO.getName(),
                            "classid", peoClassid,
                            "finish_time", voteData.getTime(),
                            "finish_rank", rank,
                            "peo_select_people", peoSelectPeoples
                    ));
                    rank++;
                    break;
                }
            }
        }

        List<String> voteResults = new ArrayList<>();
        List<VoteData> voteResultDataList = voteDataRepository.findByPid(id);
        for (VoteData voteResultData : voteResultDataList) {
            String voteTo = voteResultData.getVoteTo();
            voteResults.addAll(Arrays.asList(voteTo.split(",")));
        }

        Map<String, Long> voteCount = new HashMap<>();
        for (String voteResult : voteResults) {
            voteCount.put(voteResult, voteCount.getOrDefault(voteResult, 0L) + 1);
        }

        List<Map<String, Object>> resultArray = new ArrayList<>();
        for (Map.Entry<String, Long> entry : voteCount.entrySet()) {
            resultArray.add(Map.of("voteId", entry.getKey(), "value", entry.getValue()));
        }

        int status;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(vote.getStartTime(), formatter);
        LocalDateTime endTime = LocalDateTime.parse(vote.getEndTime(), formatter);
        if (now.isBefore(startTime)) {
            status = 1;
        } else if (!now.isAfter(endTime)) {
            status = 2;
        } else {
            status = 3;
        }

        Collection<Integer> selectPeoples = Arrays.stream(vote.getSelectPeople().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        List<UserDTO> users = userClient.getUserInCollection(selectPeoples, currentUser.getBindClass());

        List<Map<String, Object>> userMaps = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    return userMap;
                })
                .toList();


        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", vote.getId());
        responseMap.put("title", vote.getTitle());
        responseMap.put("people_num", vote.getPeopleNum());
        responseMap.put("people_num_finish", countCntNum);
        responseMap.put("list", output);
        responseMap.put("isanonymous", vote.getAnonymous());
        responseMap.put("select_num", vote.getSelectNum());
        responseMap.put("select_peoples", userMaps);
        responseMap.put("start_time", vote.getStartTime());
        responseMap.put("end_time", vote.getEndTime());
        responseMap.put("status", status);
        responseMap.put("vote_result", resultArray);

        return Result.success(responseMap);

    }
}
