package cn.ljcljc.sutuo.service.Impl;

import cn.ljcljc.api.client.UserClient;
import cn.ljcljc.api.dto.UserDTO;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.Constant;
import cn.ljcljc.common.utils.UserContext;
import cn.ljcljc.sutuo.domain.dto.CompetitionPredictDTO;
import cn.ljcljc.sutuo.domain.entity.*;
import cn.ljcljc.sutuo.repository.*;
import cn.ljcljc.sutuo.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ljc
 * @since 2024-7-19
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionArtRepository competitionArtRepository;
    private final CompetitionClassRepository competitionClassRepository;
    private final CompetitionGroupHonorRepository competitionGroupHonorRepository;
    private final CompetitionKaoheClassRepository competitionKaoheClassRepository;
    private final CompetitionKaoheLevelRepository competitionKaoheLevelRepository;
    private final CompetitionLevelRepository competitionLevelRepository;
    private final CompetitionListRepository competitionListRepository;
    private final CompetitionOtherRepository competitionOtherRepository;
    private final CompetitionPersonalHonorRepository competitionPersonalHonorRepository;
    private final CompetitionRankRepository competitionRankRepository;
    private final CompetitionRecordsRepository competitionRecordsRepository;
    private final CompetitionScoreResultRepository competitionScoreResultRepository;
    private final CompetitionScoreTableRepository competitionScoreTableRepository;
    private final CompetitionTypeRepository competitionTypeRepository;
    private final CompetitionYearRepository competitionYearRepository;
    private final UserClient userClient;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    public Result getCompetition() {
        UserInfo currentUser = UserContext.getUser();

        List<CompetitionClass> competitionClasses = competitionClassRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionLevel> competitionLevels = competitionLevelRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionList> competitionLists = competitionListRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionOther> competitionOthers = competitionOtherRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionPersonalHonor> competitionPersonalHonors = competitionPersonalHonorRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionRank> competitionRanks = competitionRankRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionType> competitionTypes = competitionTypeRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionYear> competitionYears = competitionYearRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<CompetitionKaoheLevel> competitionKaoheLevels = competitionKaoheLevelRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        List<CompetitionKaoheClass> competitionKaoheClasses = competitionKaoheClassRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        String monitor = "";

        List<UserDTO> users = userClient.getUserList(currentUser.getBindClass());
        for (UserDTO user : users) {
            if ("班长".equals(user.getZhiwu())) {
                monitor = user.getName();
            }
        }


        List<Map<String, Object>> processedCompetitionYears = competitionYears.stream().map(year -> {
            Map<String, Object> yearMap = new HashMap<>();
            yearMap.put("id", year.getId());
            yearMap.put("competitionYear", year.getCompetitionYear());
            yearMap.put("openTime", year.getOpenTime());
            yearMap.put("closeTime", year.getCloseTime());
            boolean isOpen = false;
            try {
                Date openTime = dateFormat.parse(year.getOpenTime());
                Date closeTime = dateFormat.parse(year.getCloseTime());
                Date currentTime = new Date();
                isOpen = currentTime.after(openTime) && currentTime.before(closeTime);
            } catch (ParseException e) {
                log.error("日期转换失败");
            }
            yearMap.put("isOpen", isOpen);
            return yearMap;
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("competition_classes", competitionClasses);
        data.put("competition_levels", competitionLevels);
        data.put("competition_lists", competitionLists);
        data.put("competition_others", competitionOthers);
        data.put("competition_personalhonors", competitionPersonalHonors);
        data.put("competition_ranks", competitionRanks);
        data.put("competition_types", competitionTypes);
        data.put("competition_years", processedCompetitionYears);
        data.put("competition_kaoheLevels", competitionKaoheLevels);
        data.put("competition_kaoheClasses", competitionKaoheClasses);
        data.put("monitor", monitor);

        return Result.success(data);
    }

    @Override
    public Result getRecords(Integer year) {
        UserInfo currentUser = UserContext.getUser();

        List<CompetitionRecords> records = competitionRecordsRepository.findByClassIdAndCompetitionYearAndBindClass(currentUser.getClassId(), year, currentUser.getBindClass(), Sort.by(Sort.Direction.ASC, "competitionType").and(Sort.by(Sort.Direction.ASC, "competitionClass")).and(Sort.by(Sort.Direction.ASC, "competitionRank")).and(Sort.by(Sort.Direction.ASC, "competitionLevel")).and(Sort.by(Sort.Direction.DESC, "score")));

        List<Map<String, Object>> competitionRecords = records.stream().map(record -> {
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("id", record.getId());
            recordMap.put("competitionType", record.getCompetitionType());
            recordMap.put("competitionId", record.getCompetitionId());
            recordMap.put("competitionClass", record.getCompetitionClass());
            recordMap.put("competitionRank", record.getCompetitionRank());
            recordMap.put("competitionLevel", record.getCompetitionLevel());
            recordMap.put("competitionStatus", record.getStatus());
            recordMap.put("competitionScore", record.getScore());
            recordMap.put("competitionProofURL", record.getProofURL());
            recordMap.put("competitionXingzhi", record.getCompetitionXingzhi());

            UserDTO declarer = userClient.getUser(record.getDeclarer());


            recordMap.put("declarer", record.getDeclarer());
            recordMap.put("declarerName", declarer != null ? declarer.getName() : "");

            String competitionName = switch (record.getCompetitionType()) {
                case 1 ->
                        competitionListRepository.findById(record.getCompetitionId()).map(CompetitionList::getCmptName).orElse("");
                case 2 ->
                        competitionArtRepository.findById(record.getCompetitionId()).map(CompetitionArt::getCompetitionName).orElse("");
                case 3 ->
                        competitionPersonalHonorRepository.findById(record.getCompetitionId()).map(CompetitionPersonalHonor::getHonorName).orElse("");
                case 4 ->
                        competitionGroupHonorRepository.findById(record.getCompetitionId()).map(CompetitionGroupHonor::getHonorName).orElse("");
                case 5 ->
                        competitionOtherRepository.findById(record.getCompetitionId()).map(CompetitionOther::getCompetitionName).orElse("");
                case 6 ->
                        competitionKaoheClassRepository.findById(record.getCompetitionId()).map(CompetitionKaoheClass::getCompKaoheClass).orElse("");
                default -> "";
            };
            recordMap.put("competitionName", competitionName);

            return recordMap;
        }).collect(Collectors.toList());

        CompetitionScoreResult scoreResult = competitionScoreResultRepository.findByClassId(currentUser.getClassId());
        double totalScore = (scoreResult != null && scoreResult.getTotalScore() != null) ? scoreResult.getTotalScore() : 0;
        double totalSuccessScore = (scoreResult != null && scoreResult.getTotalSuccessScore() != null) ? scoreResult.getTotalSuccessScore() : 0;

        Map<String, Object> data = new HashMap<>();
        data.put("records", competitionRecords);
        data.put("totalScore", totalScore);
        data.put("totalSuccessScore", totalSuccessScore);

        return Result.success(data);
    }

    @Override
    public Result getPredictScore(CompetitionPredictDTO competitionPredictDTO) {
        String shenbaoValue = competitionPredictDTO.getShenbaoValue();
        String compChose = competitionPredictDTO.getCompChose();
        String compXingzhi = competitionPredictDTO.getCompXingzhi();
        String compPersonChose = competitionPredictDTO.getCompPersonChose();
        String compOtherChose = competitionPredictDTO.getCompOtherChose();
        String compRank = competitionPredictDTO.getCompRank();
        String compClass = competitionPredictDTO.getCompClass();
        String compKaoheLevel = competitionPredictDTO.getCompKaoheLevel();
        String compKaoheClass = competitionPredictDTO.getCompKaoheClass();

        if (shenbaoValue == null || shenbaoValue.isEmpty()) {
            return Result.error("参数错误");
        }

        double score;
        CompetitionScoreTable scoreTable;
        double cmptWeight;
        CompetitionRank rank;
        double cmptRankScore;

        switch (shenbaoValue) {
            case "1":
                if (compChose == null || compClass == null || compRank == null) {
                    return Result.error("参数错误");
                }

                CompetitionList competitionList = competitionListRepository.findById(Integer.parseInt(compChose)).orElse(null);
                if (competitionList == null) {
                    return Result.error("参数错误");
                }

                String cmptLevel = competitionList.getCmptLevel();

                if ("1".equals(compClass) || "2".equals(compClass)) {
                    scoreTable = competitionScoreTableRepository.findByCmptTypeAndCmptLevelAndCmptClass("1", cmptLevel, compClass);
                } else {
                    scoreTable = competitionScoreTableRepository.findByCmptTypeAndCmptClass("1", compClass);
                }

                if (scoreTable == null) {
                    return Result.error("参数错误");
                }

                cmptWeight = scoreTable.getCmptWeight();

                rank = competitionRankRepository.findById(Integer.parseInt(compRank)).orElse(null);
                if (rank == null) {
                    return Result.error("参数错误");
                }

                cmptRankScore = rank.getBaseScore();

                score = cmptRankScore * cmptWeight;

                if ("2".equals(compXingzhi)) {
                    score *= 0.5;
                }

                score = Math.round(score * 100.0) / 100.0;
                break;

            case "2":
                if (compClass == null || compRank == null) {
                    return Result.error("参数错误");
                }

                scoreTable = competitionScoreTableRepository.findByCmptTypeAndCmptClass("1", compClass);
                if (scoreTable == null) {
                    return Result.error("参数错误");
                }

                cmptWeight = scoreTable.getCmptWeight();

                rank = competitionRankRepository.findById(Integer.parseInt(compRank)).orElse(null);
                if (rank == null) {
                    return Result.error("参数错误");
                }

                cmptRankScore = rank.getBaseScore();
                score = cmptRankScore * cmptWeight;
                score = Math.round(score * 100.0) / 100.0;
                break;

            case "3":
                if (compClass == null || compPersonChose == null) {
                    return Result.error("参数错误");
                }

                CompetitionPersonalHonor personalHonor = competitionPersonalHonorRepository.findById(Integer.parseInt(compPersonChose)).orElse(null);
                if (personalHonor == null) {
                    return Result.error("参数错误");
                }

                double honorWeight = personalHonor.getHonorWeight();

                CompetitionClass competitionClass = competitionClassRepository.findById(Integer.parseInt(compClass)).orElse(null);
                if (competitionClass == null) {
                    return Result.error("参数错误");
                }

                double cmptClassScore = competitionClass.getWeight();
                score = honorWeight * cmptClassScore;
                score = Math.round(score * 100.0) / 100.0;
                break;

            case "5":
                if (compOtherChose == null) {
                    return Result.error("参数错误");
                }

                CompetitionOther competitionOther = competitionOtherRepository.findById(Integer.parseInt(compOtherChose)).orElse(null);
                if (competitionOther == null) {
                    return Result.error("参数错误");
                }

                score = competitionOther.getScore();
                break;

            case "6":
                if (compKaoheLevel == null || compKaoheClass == null) {
                    return Result.error("参数错误");
                }

                CompetitionKaoheLevel kaoheLevel = competitionKaoheLevelRepository.findById(Integer.parseInt(compKaoheLevel)).orElse(null);
                if (kaoheLevel == null) {
                    return Result.error("参数错误");
                }

                double cmptKaoheLevelScore = kaoheLevel.getScore();

                CompetitionKaoheClass kaoheClass = competitionKaoheClassRepository.findById(Integer.parseInt(compKaoheClass)).orElse(null);
                if (kaoheClass == null) {
                    return Result.error("参数错误");
                }

                double cmptKaoheClassWeight = kaoheClass.getWeight();
                score = cmptKaoheLevelScore * cmptKaoheClassWeight;
                score = Math.round(score * 100.0) / 100.0;
                break;

            default:
                return Result.error("参数错误");
        }

        return Result.success(score);
    }

    @Override
    public Result upload(String shenbaoYearValue, String shenbaoValue, String compChose, String compXingzhi, String compPersonChose, String compOtherChose, String comptWentiTitle, String compRank, String compClass, String compKaoheLevel, String compKaoheClass, MultipartFile file) {
        UserInfo currentUser = UserContext.getUser();

        CompetitionYear competitionYear = competitionYearRepository.findById(Integer.parseInt(shenbaoYearValue)).orElse(null);
        if (competitionYear == null) {
            return Result.error("参数错误");
        }

        try {
            Date openTime = dateFormat.parse(competitionYear.getOpenTime());
            Date closeTime = dateFormat.parse(competitionYear.getCloseTime());
            Date currentTime = new Date();
            if (currentTime.before(openTime) || currentTime.after(closeTime)) {
                return Result.error("不在申报时间范围内");
            }
        } catch (ParseException e) {
            return Result.error("日期解析错误");
        }

        String compXingzhi_CHZ = "1".equals(compXingzhi) ? "个人项目" : "团队项目";
        Integer comptId = (compChose != null && !compChose.isEmpty() && !"null".equals(compChose)) ? Integer.parseInt(compChose) : null;

        if (shenbaoValue == null) {
            return Result.error("参数错误");
        }

        if ("6".equals(shenbaoValue)) {
            compRank = compKaoheLevel;
        }

        double score = 0.0;
        String cmptLevel = null;
        String cmptRankName = null;
        String cmptClassName = null;

        switch (shenbaoValue) {
            case "1":
                if (compChose == null || compClass == null || compRank == null || compChose.isEmpty() || compClass.isEmpty() || compRank.isEmpty() ||
                        "null".equals(compChose) || "null".equals(compClass) || "null".equals(compRank)) {
                    return Result.error("参数错误");
                }
                CompetitionList competitionList = competitionListRepository.findById(Integer.parseInt(compChose)).orElse(null);
                cmptLevel = competitionList != null ? competitionList.getCmptLevel() : null;
                CompetitionScoreTable scoreTable1 = ("1".equals(compClass) || "2".equals(compClass))
                        ? competitionScoreTableRepository.findByCmptTypeAndCmptLevelAndCmptClass("1", cmptLevel, compClass)
                        : competitionScoreTableRepository.findByCmptTypeAndCmptClass("1", compClass);
                double cmptWeight1 = scoreTable1 != null ? scoreTable1.getCmptWeight() : 0;
                CompetitionRank rank1 = competitionRankRepository.findById(Integer.parseInt(compRank)).orElse(null);
                double cmptRankScore1 = rank1 != null ? rank1.getBaseScore() : 0;
                cmptRankName = rank1 != null ? rank1.getCompetitionRank() : null;
                CompetitionClass competitionClass1 = competitionClassRepository.findById(Integer.parseInt(compClass)).orElse(null);
                cmptClassName = competitionClass1 != null ? competitionClass1.getCompetitionClass() : null;
                score = cmptRankScore1 * cmptWeight1;
                if ("2".equals(compXingzhi)) {
                    score *= 0.5;
                }
                break;
            case "2":
                if (compClass == null || compRank == null || compClass.isEmpty() || compRank.isEmpty() || "null".equals(compClass) || "null".equals(compRank)) {
                    return Result.error("参数错误");
                }
                CompetitionScoreTable scoreTable2 = competitionScoreTableRepository.findByCmptTypeAndCmptClass("1", compClass);
                double cmptWeight2 = scoreTable2 != null ? scoreTable2.getCmptWeight() : 0;
                CompetitionRank rank2 = competitionRankRepository.findById(Integer.parseInt(compRank)).orElse(null);
                double cmptRankScore2 = rank2 != null ? rank2.getBaseScore() : 0;
                cmptRankName = rank2 != null ? rank2.getCompetitionRank() : null;
                CompetitionClass competitionClass2 = competitionClassRepository.findById(Integer.parseInt(compClass)).orElse(null);
                cmptClassName = competitionClass2 != null ? competitionClass2.getCompetitionClass() : null;
                score = cmptRankScore2 * cmptWeight2;
                break;
            case "3":
                if (compClass == null || compPersonChose == null || compClass.isEmpty() || compPersonChose.isEmpty() || "null".equals(compClass) || "null".equals(compPersonChose)) {
                    return Result.error("参数错误");
                }
                CompetitionPersonalHonor personalHonor = competitionPersonalHonorRepository.findById(Integer.parseInt(compPersonChose)).orElse(null);
                double honorWeight = personalHonor != null ? personalHonor.getHonorWeight() : 0;
                CompetitionClass competitionClass3 = competitionClassRepository.findById(Integer.parseInt(compClass)).orElse(null);
                double cmptClassScore = competitionClass3 != null ? competitionClass3.getWeight() : 0;
                cmptClassName = competitionClass3 != null ? competitionClass3.getCompetitionClass() : null;
                score = honorWeight * cmptClassScore;
                break;
            case "5":
                if (compOtherChose == null || compOtherChose.isEmpty() || "null".equals(compOtherChose)) {
                    return Result.error("参数错误");
                }
                CompetitionOther competitionOther = competitionOtherRepository.findById(Integer.parseInt(compOtherChose)).orElse(null);
                if (competitionOther != null) {
                    score = competitionOther.getScore();
                }
                break;
            case "6":
                if (compKaoheLevel == null || compKaoheClass == null || compKaoheLevel.isEmpty() || compKaoheClass.isEmpty()) {
                    return Result.error("参数错误");
                }
                CompetitionKaoheLevel kaoheLevel = competitionKaoheLevelRepository.findById(Integer.parseInt(compKaoheLevel)).orElse(null);
                double cmptKaoheLevelScore = kaoheLevel != null ? kaoheLevel.getScore() : 0;
                CompetitionKaoheClass kaoheClass = competitionKaoheClassRepository.findById(Integer.parseInt(compKaoheClass)).orElse(null);
                double cmptKaoheClassWeight = kaoheClass != null ? kaoheClass.getWeight() : 0;
                score = cmptKaoheLevelScore * cmptKaoheClassWeight;
                break;
        }

        score = Math.round(score * 100.0) / 100.0;


        String competitionFileName = "";
        switch (shenbaoValue) {
            case "1":
                CompetitionList list = competitionListRepository.findById(Integer.parseInt(compChose)).orElse(null);
                competitionFileName = list != null ? list.getCmptName() : "";
                comptId = Integer.parseInt(compChose);
                break;
            case "2":
                competitionFileName = comptWentiTitle;
                CompetitionArt art = new CompetitionArt();
                art.setCompetitionName(comptWentiTitle);
                competitionArtRepository.save(art);
                comptId = art.getId();
                break;
            case "3":
                CompetitionPersonalHonor honor = competitionPersonalHonorRepository.findById(Integer.parseInt(compPersonChose)).orElse(null);
                competitionFileName = honor != null ? honor.getHonorName() : "";
                comptId = honor != null ? honor.getId() : 0;
                break;
            case "5":
                CompetitionOther other = competitionOtherRepository.findById(Integer.parseInt(compOtherChose)).orElse(null);
                competitionFileName = other != null ? other.getCompetitionName() : "";
                comptId = Integer.parseInt(compOtherChose);
                break;
            case "6":
                CompetitionKaoheClass kaoheClass = competitionKaoheClassRepository.findById(Integer.parseInt(compKaoheClass)).orElse(null);
                competitionFileName = kaoheClass != null ? kaoheClass.getCompKaoheClass() : "";
                comptId = Integer.parseInt(compKaoheClass);
                break;
        }

        String extension = Optional.ofNullable(file.getOriginalFilename()).filter(f -> f.contains(".")).map(f -> f.substring(file.getOriginalFilename().lastIndexOf(".") + 1)).orElse("");

        String randNum = UUID.randomUUID().toString().substring(0, 6);

        // 获取项目根目录
        String rootDirectory = System.getProperty("user.dir");

        // 构建文件存储路径
        String url3 = rootDirectory + File.separator + "public" + File.separator + "sutuo" + File.separator + "2024" + File.separator + currentUser.getBindClass() + File.separator + currentUser.getName() + File.separator;

        log.info(url3);
        File directory = new File(url3);
        if (!directory.exists() && !directory.mkdirs()) {
            return Result.error("创建目录失败：" + url3);
        }


        String confname1;
        if ("1".equals(shenbaoValue) || "2".equals(shenbaoValue)) {
            confname1 = competitionFileName + '-' + cmptClassName + '-' + cmptRankName;
        } else if ("3".equals(shenbaoValue)) {
            confname1 = competitionFileName + '-' + cmptClassName;
        } else {
            confname1 = competitionFileName;
        }

        String fileName = confname1 + randNum + '.' + extension;
        File destinationFile = new File(url3 + fileName);


        try {
            file.transferTo(destinationFile);
        } catch (IOException e) {
            log.error("文件上传失败：" + e.getMessage());
            return Result.error("文件上传失败：" + e.getMessage());
        }

        String proofURL = "/2024/" + currentUser.getBindClass() + "/" + currentUser.getName() + "/" + fileName;

        CompetitionRecords record = new CompetitionRecords();
        record.setBindClass(currentUser.getBindClass());
        record.setCompetitionType(Integer.parseInt(shenbaoValue));
        record.setCompetitionId(comptId);
        record.setCompetitionClass((compClass != null && !compClass.isEmpty() && !"null".equals(compClass)) ? Integer.parseInt(compClass) : 0);
        record.setCompetitionRank((compRank != null && !compRank.isEmpty() && !"null".equals(compRank)) ? Integer.parseInt(compRank) : 0);
        record.setCompetitionLevel((cmptLevel != null && !cmptLevel.isEmpty() && !"null".equals(cmptLevel)) ? Integer.parseInt(cmptLevel) : 0);
        record.setCompetitionXingzhi(compXingzhi_CHZ);
        record.setCompetitionYear(Integer.parseInt(shenbaoYearValue));
        record.setStatus(Constant.USER_SUTUO_SHENHE_WAITING);
        record.setClassId(currentUser.getClassId());
        record.setDeclarer(currentUser.getClassId());
        record.setScore(score);
        record.setProofURL(proofURL);

        competitionRecordsRepository.save(record);

        return Result.success(null);
    }

    @Override
    @Transactional
    public Result rollback(Integer id) {
        UserInfo currentUser = UserContext.getUser();

        Optional<CompetitionRecords> optionalRecord = competitionRecordsRepository.findByIdAndDeclarer(id, currentUser.getClassId());
        if (optionalRecord.isEmpty()) {
            return Result.error("参数错误");
        }

        CompetitionRecords record = optionalRecord.get();
        String proofURL = record.getProofURL();
        String recordClassid = record.getDeclarer();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            if (recordClassid == null || !recordClassid.equals(currentUser.getClassId())) {
                return Result.error("无权限");
            }
        }

        // 获取项目根目录
        String rootDirectory = System.getProperty("user.dir");

        // 构建文件存储路径
        String baseUrl = rootDirectory + File.separator + "public" + File.separator + "sutuo";

        if (proofURL != null && !proofURL.isEmpty()) {
            File file = new File(baseUrl + proofURL);
            if (file.exists()) {
                if (!file.delete()) {
                    return Result.error("文件删除失败");
                }
            }
        }

        competitionRecordsRepository.deleteById(id);

        return Result.success();
    }

    @Override
    public Result getAllRecords(Integer year) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        List<CompetitionRecords> records = competitionRecordsRepository.findByCompetitionYearAndBindClass(year, currentUser.getBindClass(), Sort.by(Sort.Direction.ASC, "status").and(Sort.by(Sort.Direction.ASC, "classId")).and(Sort.by(Sort.Direction.ASC, "competitionType")).and(Sort.by(Sort.Direction.ASC, "competitionClass")).and(Sort.by(Sort.Direction.ASC, "competitionRank")).and(Sort.by(Sort.Direction.ASC, "competitionLevel")).and(Sort.by(Sort.Direction.DESC, "score")));

        List<Map<String, Object>> competitionRecords = records.stream().map(record -> {
            Map<String, Object> recordMap = new HashMap<>();
            recordMap.put("id", record.getId());
            recordMap.put("competitionType", record.getCompetitionType());
            recordMap.put("competitionId", record.getCompetitionId());
            recordMap.put("competitionClass", record.getCompetitionClass());
            recordMap.put("competitionRank", record.getCompetitionRank());
            recordMap.put("competitionLevel", record.getCompetitionLevel());
            recordMap.put("competitionStatus", record.getStatus());
            recordMap.put("competitionScore", record.getScore());
            recordMap.put("competitionProofURL", record.getProofURL());
            recordMap.put("competitionXingzhi", record.getCompetitionXingzhi());
            recordMap.put("declarer", record.getDeclarer());

            UserDTO declarer = userClient.getUser(record.getDeclarer());
            recordMap.put("declarerName", declarer != null ? declarer.getName() : "");

            UserDTO beneficiary = userClient.getUser(record.getClassId());
            recordMap.put("BeneficiaryName", beneficiary != null ? beneficiary.getName() : "");

            String competitionName = switch (record.getCompetitionType()) {
                case 1 ->
                        competitionListRepository.findById(record.getCompetitionId()).map(CompetitionList::getCmptName).orElse("");
                case 2 ->
                        competitionArtRepository.findById(record.getCompetitionId()).map(CompetitionArt::getCompetitionName).orElse("");
                case 3 ->
                        competitionPersonalHonorRepository.findById(record.getCompetitionId()).map(CompetitionPersonalHonor::getHonorName).orElse("");
                case 4 ->
                        competitionGroupHonorRepository.findById(record.getCompetitionId()).map(CompetitionGroupHonor::getHonorName).orElse("");
                case 5 ->
                        competitionOtherRepository.findById(record.getCompetitionId()).map(CompetitionOther::getCompetitionName).orElse("");
                case 6 ->
                        competitionKaoheClassRepository.findById(record.getCompetitionId()).map(CompetitionKaoheClass::getCompKaoheClass).orElse("");
                default -> "";
            };
            recordMap.put("competitionName", competitionName);

            return recordMap;
        }).collect(Collectors.toList());

        return Result.success(competitionRecords);
    }

    @Override
    @Transactional
    public Result getTotalScore(Integer year) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (year == null) {
            return Result.error("参数错误");
        }

        List<CompetitionScoreResult> totalScores = competitionScoreResultRepository.findByCompetitionYearAndBindClass(year, currentUser.getBindClass());
        List<Map<String, Object>> output = new ArrayList<>();
        int count = 0;

        for (CompetitionScoreResult scoreResult : totalScores) {
            count++;
            String classid = scoreResult.getClassId();
            double totalScore = scoreResult.getTotalScore() != null ? scoreResult.getTotalScore() : 0;
            double totalSuccessScore = scoreResult.getTotalSuccessScore() != null ? scoreResult.getTotalSuccessScore() : 0;

            UserDTO user = userClient.getUser(classid);
            String username = user != null ? user.getName() : "Unknown";

            Map<String, Object> scoreMap = new HashMap<>();
            scoreMap.put("id", count);
            scoreMap.put("classid", classid);
            scoreMap.put("name", username);
            scoreMap.put("totalScore", totalScore);
            scoreMap.put("totalSuccessScore", totalSuccessScore);
            output.add(scoreMap);
        }

        return Result.success(output);
    }

    @Override
    public Result handleShenhe(String mode, Integer id) {
        UserInfo currentUser = UserContext.getUser();

        if (!Constant.PERMISSION_GROUP_SUPER_ADMIN.equals(currentUser.getUserGroup())) {
            return Result.error("无权限");
        }

        if (id == null || mode == null || mode.isEmpty()) {
            return Result.error("参数错误");
        }

        Optional<CompetitionRecords> optionalRecord = competitionRecordsRepository.findByIdAndBindClass(id, currentUser.getBindClass());
        if (optionalRecord.isEmpty()) {
            return Result.error("没有数据");
        }

        CompetitionRecords record = optionalRecord.get();
        if ("accept".equals(mode)) {
            record.setStatus(Constant.USER_SUTUO_SHENHE_ACCEPT);
        } else {
            record.setStatus(Constant.USER_SUTUO_SHENHE_REJEFCT);
        }
        competitionRecordsRepository.save(record);

        return Result.success(null);
    }
}
