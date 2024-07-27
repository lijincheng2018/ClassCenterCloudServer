package cn.ljcljc.sutuo.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.sutuo.domain.dto.CompetitionPredictDTO;
import cn.ljcljc.sutuo.service.CompetitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "素拓申报接口", description = "用于素拓申报相关操作")
@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {
    private final CompetitionService competitionService;

    @Autowired
    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @Operation(summary = "用户获取系统申报信息")
    @GetMapping
    public Result getCompetitions() {
        return competitionService.getCompetition();
    }

    @Operation(summary = "用户查询个人某年申报记录")
    @GetMapping("/{year}")
    public Result getRecords(@PathVariable Integer year) {
        return competitionService.getRecords(year);
    }

    @Operation(summary = "用户查询预计素拓成绩")
    @PostMapping("/predict-score")
    public Result getPredictScore(@RequestBody CompetitionPredictDTO competitionPredictDTO) {
        return competitionService.getPredictScore(competitionPredictDTO);
    }

    @Operation(summary = "用户申报")
    @PostMapping
    public Result upload(
            @RequestParam("shenbaoYearValue") String shenbaoYearValue,
            @RequestParam("shenbaoValue") String shenbaoValue,
            @RequestParam("compChose") String compChose,
            @RequestParam("compXingzhi") String compXingzhi,
            @RequestParam("compPersonChose") String compPersonChose,
            @RequestParam("compOtherChose") String compOtherChose,
            @RequestParam("comptWentiTitle") String comptWentiTitle,
            @RequestParam("compRank") String compRank,
            @RequestParam("compClass") String compClass,
            @RequestParam("compKaoheLevel") String compKaoheLevel,
            @RequestParam("compKaoheClass") String compKaoheClass,
            @RequestParam("file") MultipartFile file) {

        return competitionService.upload(shenbaoYearValue, shenbaoValue, compChose, compXingzhi, compPersonChose, compOtherChose, comptWentiTitle, compRank, compClass, compKaoheLevel, compKaoheClass, file);
    }

    @Operation(summary = "用户撤回申报记录")
    @DeleteMapping("/{id}")
    public Result rollback(@PathVariable Integer id) {
        return competitionService.rollback(id);
    }

    @Operation(summary = "管理员查询某年所有申报记录")
    @GetMapping("/admin/records/{year}")
    public Result getAllRecords(@PathVariable Integer year) {
        return competitionService.getAllRecords(year);
    }

    @Operation(summary = "管理员查询某年所有人的素拓成绩")
    @GetMapping("/admin/totScore/{year}")
    public Result getTotalScore(@PathVariable Integer year) {
        return competitionService.getTotalScore(year);
    }

    @Operation(summary = "管理员审核申报记录")
    @PutMapping("/admin/{id}")
    public Result handleShenhe(@RequestParam String mode, @PathVariable Integer id) {
        return competitionService.handleShenhe(mode, id);
    }
}
