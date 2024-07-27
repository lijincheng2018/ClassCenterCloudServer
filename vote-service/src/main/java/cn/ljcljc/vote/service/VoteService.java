package cn.ljcljc.vote.service;

import cn.ljcljc.common.domain.Result;

public interface VoteService {
    Result listVotes();
    Result getVoteList();
    Result getVoteDetail(Integer id);
    Result createVote(String title, String people, String selectPeople, String startTime, String endTime, Boolean anonymous, Integer selectNum);
    Result editVote(Integer id, String title, String startTime, String endTime);
    Result deleteVote(Integer id);
    Result vote(Integer id, String selectPeople);
    Result getVoteSummary(Integer id);
}
