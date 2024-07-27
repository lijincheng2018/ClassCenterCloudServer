package cn.ljcljc.sutuo.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.sutuo.domain.dto.CompetitionPredictDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CompetitionService {
    Result getCompetition();

    Result getRecords(Integer year);

    Result getPredictScore(CompetitionPredictDTO competitionPredictDTO);
    Result upload(String shenbaoYearValue,
                  String shenbaoValue,
                  String compChose,
                  String compXingzhi,
                  String compPersonChose,
                  String compOtherChose,
                  String comptWentiTitle,
                  String compRank,
                  String compClass,
                  String compKaoheLevel,
                  String compKaoheClass,
                  MultipartFile file);

    Result rollback(Integer id);

    Result getAllRecords(Integer year);

    Result getTotalScore(Integer year);

    Result handleShenhe(String mode, Integer id);

}
