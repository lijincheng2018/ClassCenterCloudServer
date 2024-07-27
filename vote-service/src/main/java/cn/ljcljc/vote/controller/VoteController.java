package cn.ljcljc.vote.controller;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.vote.domain.dto.VoteDTO;
import cn.ljcljc.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "投票管理", description = "用于处理投票相关操作")
@RestController
@RequestMapping("/api/vote")
public class VoteController {
    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @Operation(summary = "获取投票列表")
    @GetMapping
    public Result listVotes() {
        return voteService.listVotes();
    }

    @Operation(summary = "用户获取投票详情")
    @GetMapping("/{id}")
    public Result getVoteDetail(@PathVariable Integer id) {
        return voteService.getVoteDetail(id);
    }

    @Operation(summary = "提交投票")
    @PostMapping
    public Result vote(@RequestParam Integer id, @RequestParam String selectPeople) {
        return voteService.vote(id, selectPeople);
    }

    @Operation(summary = "管理员获取投票列表")
    @GetMapping("/admin")
    public Result getVoteList() {
        return voteService.getVoteList();
    }

    @Operation(summary = "管理员创建投票")
    @PostMapping("/admin")
    public Result createVote(@RequestBody VoteDTO voteDTO) {
        return voteService.createVote(
                voteDTO.getTitle(),
                voteDTO.getPeople(),
                voteDTO.getSelect_people(),
                voteDTO.getStart_time(),
                voteDTO.getEnd_time(),
                voteDTO.getIsanonymous(),
                voteDTO.getSelect_num());
    }

    @Operation(summary = "管理员更新投票信息")
    @PutMapping("/admin/{id}")
    public Result editVote(@PathVariable Integer id, @RequestBody VoteDTO voteDTO) {
        return voteService.editVote(id, voteDTO.getTitle(), voteDTO.getStart_time(), voteDTO.getEnd_time());
    }

    @Operation(summary = "管理员删除投票")
    @DeleteMapping("/admin/{id}")
    public Result deleteVote(@PathVariable Integer id) {
        return voteService.deleteVote(id);
    }

    @Operation(summary = "管理员获取投票结果")
    @GetMapping("/admin/result/{id}")
    public Result getVoteSummary(@PathVariable Integer id) {
        return voteService.getVoteSummary(id);
    }
}
