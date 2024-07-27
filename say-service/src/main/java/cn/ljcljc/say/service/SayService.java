package cn.ljcljc.say.service;

import cn.ljcljc.common.domain.Result;
import cn.ljcljc.say.domain.dto.SayDTO;
import cn.ljcljc.say.domain.entity.Say;

import java.util.List;

public interface SayService {
    List<Say> getSay(String bindClassId, Integer banwei);
    List<Say> getUnReadSay(String bindClassId, Integer banwei);
    Result getContent(Integer id);
    Result addNewSay(SayDTO sayDTO);
    Result listSays();
    Result getContentBanwei(Integer id);
    Result listSaysBanwei();
    Result postReply(Integer id, String reply);
}
