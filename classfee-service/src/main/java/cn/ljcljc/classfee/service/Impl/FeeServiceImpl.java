package cn.ljcljc.classfee.service.Impl;

import cn.ljcljc.classfee.domain.dto.FeeDTO;
import cn.ljcljc.classfee.domain.entity.Fee;
import cn.ljcljc.classfee.domain.entity.SystemInfo;
import cn.ljcljc.classfee.domain.vo.FeeVO;
import cn.ljcljc.classfee.repository.FeeRepository;
import cn.ljcljc.classfee.repository.SystemInfoRepository;
import cn.ljcljc.classfee.service.FeeService;
import cn.ljcljc.common.domain.Result;
import cn.ljcljc.common.domain.pojo.UserInfo;
import cn.ljcljc.common.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ljc
 * @since 2024-7-18
 */

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;
    private final SystemInfoRepository systemInfoRepository;

    @Autowired
    public FeeServiceImpl(FeeRepository feeRepository, SystemInfoRepository systemInfoRepository) {
        this.feeRepository = feeRepository;
        this.systemInfoRepository = systemInfoRepository;
    }

    @Override
    @Transactional
    public Result setNewFee(FeeDTO feeDTO) {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null || (!"1".equals(currentUser.getUserGroup()) && !"生劳委员".equals(currentUser.getZhiwu()))) {
            return Result.error("没有权限");
        }

        if (feeDTO.getTitle() == null || feeDTO.getFee() == null || feeDTO.getStatus() == null) {
            return Result.error("参数错误");
        }

        SystemInfo systemInfo = systemInfoRepository.findByTagAndBindClass("classmoney", currentUser.getBindClass());
        int classmoney = Integer.parseInt(systemInfo.getContent());

        if ("1".equals(feeDTO.getStatus())) {
            classmoney -= Integer.parseInt(feeDTO.getFee());
        } else if ("2".equals(feeDTO.getStatus())) {
            classmoney += Integer.parseInt(feeDTO.getFee());
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Fee newFee = new Fee();
        newFee.setBindClass(currentUser.getBindClass());
        newFee.setTitle(feeDTO.getTitle());
        newFee.setFee(feeDTO.getFee());
        newFee.setAfterMoney(String.valueOf(classmoney));
        newFee.setMethod(feeDTO.getStatus());
        newFee.setAuthor(currentUser.getName());
        newFee.setTime(formattedDateTime);

        try {
            feeRepository.save(newFee);
            systemInfo.setContent(String.valueOf(classmoney));
            systemInfoRepository.save(systemInfo);

            FeeVO feeVO = new FeeVO();
            feeVO.setId(newFee.getId());
            feeVO.setTitle(newFee.getTitle());
            feeVO.setMoney(newFee.getFee());
            feeVO.setAfter_money(newFee.getAfterMoney());
            feeVO.setMethod(newFee.getMethod());
            feeVO.setPoster(newFee.getAuthor());
            feeVO.setTime(newFee.getTime());

            return Result.success(feeVO);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Override
    public Result getFeeList() {
        UserInfo currentUser = UserContext.getUser();

        if (currentUser == null) {
            return Result.error("未登录");
        }

        List<Fee> feeList = feeRepository.findByBindClassOrderByIdDesc(currentUser.getBindClass());
        List<FeeVO> feeVOList = new ArrayList<>();

        for (Fee fee : feeList) {
            FeeVO feeVO = new FeeVO();
            feeVO.setId(fee.getId());
            feeVO.setTitle(fee.getTitle());
            feeVO.setMoney(fee.getFee());
            feeVO.setAfter_money(fee.getAfterMoney());
            feeVO.setMethod(fee.getMethod());
            feeVO.setPoster(fee.getAuthor());
            feeVO.setTime(fee.getTime());
            feeVOList.add(feeVO);
        }


        Map<String, Object> response = new HashMap<>();
        response.put("usergroup", currentUser.getUserGroup());
        response.put("results", feeVOList);

        return Result.success(response);
    }
}
