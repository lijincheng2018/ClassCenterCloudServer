package cn.ljcljc.task.service.Impl;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.task.domain.entity.*;
import cn.ljcljc.task.domain.vo.TaskResponseVO;
import cn.ljcljc.task.domain.vo.TaskVO;
import cn.ljcljc.task.service.TaskTableService;
import cn.ljcljc.common.utils.ClassCenterUtils;
import cn.ljcljc.common.utils.UserContext;
import cn.ljcljc.task.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ljc
 * @since 2024-7-18
 */

@Service
public class TaskTableServiceImpl implements TaskTableService {

    private final TaskTableRepository taskTableRepository;

    private final RegisterListRepository registerListRepository;

    private final RegisterDataRepository registerDataRepository;

    private final CollectListRepository collectListRepository;

    private final CollectDataRepository collectDataRepository;

    @Autowired
    public TaskTableServiceImpl(TaskTableRepository taskTableRepository, RegisterListRepository registerListRepository, RegisterDataRepository registerDataRepository, CollectListRepository collectListRepository, CollectDataRepository collectDataRepository) {
        this.taskTableRepository = taskTableRepository;
        this.registerListRepository = registerListRepository;
        this.registerDataRepository = registerDataRepository;
        this.collectListRepository = collectListRepository;
        this.collectDataRepository = collectDataRepository;
    }

    @Override
    @Transactional
    public void addTaskTable(String taskName, Integer pid, String leixing, String time) {
        TaskTable taskTable = new TaskTable();
        taskTable.setPid(pid);
        taskTable.setLeixing(leixing);
        taskTable.setTitle(taskName);
        taskTable.setTime(time);
        taskTableRepository.save(taskTable);
    }

    public void deleteTaskTableByPid(Integer pid) {
        taskTableRepository.deleteByPid(pid);
    }

    @Override
    public void updateTaskTableName(Integer pid, String taskName) {
        TaskTable taskTable = taskTableRepository.findByPid(pid);

        taskTable.setTitle(taskName);

        taskTableRepository.save(taskTable);
    }

    @Override
    public Result listTasks(String method) {
        UserInfo currentUser = UserContext.getUser();

        int aId = 0;
        int bId = 0;
        int finishNum = 0;

        List<TaskVO> output = new ArrayList<>();

        List<TaskTable> taskTables = taskTableRepository.findAllByOrderByIdDesc();

        for (TaskTable task : taskTables) {
            String leixing = task.getLeixing();
            Integer pid = task.getPid();

            if ("1".equals(leixing)) {
                RegisterList registerList = registerListRepository.findByIdAndBindClass(pid, currentUser.getBindClass()).orElse(null);
                if (registerList != null) {
                    String title = registerList.getTitle();
                    String time = registerList.getTime();
                    String author = registerList.getAuthor();
                    boolean isPublic = "1".equals(registerList.getIsPublic());

                    if (isPublic) {
                        List<String> peoples = List.of(registerList.getPeople().split(","));

                        boolean ifOutTime = "true".equals(registerList.getIfOutTime());
                        String jieZhiTime = registerList.getTimeFrame();

                        boolean ifLimit = "true".equals(registerList.getIfLimit());
                        String startTime = registerList.getStartTime();
                        String endTime = registerList.getEndTime();

                        int status = ClassCenterUtils.determineStatus(ifOutTime, ifLimit, jieZhiTime, startTime, endTime);

                        for (String people : peoples) {
                            if (people.equals(String.valueOf(currentUser.getUid()))) {
                                aId++;
                                bId++;
                                RegisterData registerData = registerDataRepository.findByUidAndRegisterId(currentUser.getUid(), pid);
                                boolean mc = !"0".equals(registerData.getStatus());
                                if (mc) finishNum++;

                                if ("limit".equals(method)) {
                                    if (ifLimit) {
                                        output.add(new TaskVO(aId, pid, title, mc, leixing, author, time, ifLimit, startTime, endTime, status));
                                    } else {
                                        output.add(new TaskVO(aId, pid, title, mc, leixing, author, time, ifLimit, jieZhiTime, status));
                                    }
                                } else {
                                    output.add(new TaskVO(aId, pid, title, mc, leixing, author, time, jieZhiTime, status));
                                }
                                break;
                            }
                        }
                    }
                }
            }

            if ("2".equals(leixing)) {
                CollectList collectList = collectListRepository.findByIdAndBindClass(pid, currentUser.getBindClass()).orElse(null);

                if (collectList != null) {
                    String title = collectList.getTitle();
                    String time = collectList.getTime();
                    String author = collectList.getAuthor();
                    String jieZhiTime = collectList.getTimeFrame();

                    List<String> peoples = List.of(collectList.getPeople().split(","));

                    boolean ifOutTime = "true".equals(collectList.getIfOutTime());
                    String startTime = collectList.getStartTime();
                    String endTime = collectList.getEndTime();
                    boolean ifLimit = "true".equals(collectList.getIfLimit());
                    boolean ifShenhe = "true".equals(collectList.getIfShenhe());
                    boolean isNeed = "true".equals(collectList.getIsNeed());

                    int status = ClassCenterUtils.determineStatus(ifOutTime, ifLimit, jieZhiTime, startTime, endTime);

                    for (String people : peoples) {
                        if (people.equals(String.valueOf(currentUser.getUid()))) {
                            if (isNeed) bId++;
                            aId++;

                            CollectData collectData = collectDataRepository.findByUidAndCollectId(currentUser.getUid(), pid);
                            boolean mc = !"0".equals(collectData.getStatus());
                            if (mc && isNeed) finishNum++;

                            if ("limit".equals(method)) {
                                if (ifLimit) {
                                    output.add(new TaskVO(aId, pid, title, mc, leixing, author, time, ifLimit, startTime, endTime, status, ifShenhe, isNeed));
                                } else {
                                    output.add(new TaskVO(aId, pid, title, mc, leixing, author, time, ifLimit, jieZhiTime, status, ifShenhe, isNeed));
                                }
                            } else {
                                if (jieZhiTime.isEmpty()) jieZhiTime = endTime;
                                output.add(new TaskVO(aId, pid, title, mc, leixing, author, time, jieZhiTime, status, ifShenhe, isNeed));
                            }
                            break;
                        }
                    }
                }
            }
        }

        if ("limit".equals(method)) {
            return Result.success(new TaskResponseVO(bId, finishNum, output.subList(0, Math.min(5, output.size()))));
        } else {
            return Result.success(new TaskResponseVO(bId, finishNum, output));
        }
    }

}
