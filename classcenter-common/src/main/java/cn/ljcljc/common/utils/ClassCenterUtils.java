package cn.ljcljc.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ljc
 * @since 2024-07-17
 */
public class ClassCenterUtils {
    /**
     * 班委职务转换为数字
     *
     * @param zhiwu 班委职务中文名称
     * @return 班委职务对应数字
     */
    public static Integer converZhiwuToBanwei(String zhiwu) {
        return switch (zhiwu) {
            case "班长" -> Constant.USER_BANWEI_BANZHANG;
            case "副班长" -> Constant.USER_BANWEI_FUBANZHANG;
            case "团支书" -> Constant.USER_BANWEI_TUANZHISHU;
            case "学习委员" -> Constant.USER_BANWEI_XUEXIWEIYUAN;
            case "组织委员" -> Constant.USER_BANWEI_ZUZHIWEIYUAN;
            case "文体委员" -> Constant.USER_BANWEI_WENTIWEIYUAN;
            case "生劳委员" -> Constant.USER_BANWEI_SHENGLAOWEIYUAN;
            case "纪律委员" -> Constant.USER_BANWEI_JILVYUAN;
            default -> 0;
        };
    }

    public static int determineStatus(boolean ifOutTime, boolean ifLimit, String jieZhiTime, String start_time, String end_time) {
        LocalDateTime now = LocalDateTime.now();
        if (ifLimit) {
            LocalDateTime startTime = LocalDateTime.parse(start_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(end_time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (now.isBefore(startTime)) {
                return 1;
            } else if (now.isAfter(endTime)) {
                return 3;
            } else {
                return 2;
            }
        } else if (ifOutTime) {
            LocalDateTime timeFrame = LocalDateTime.parse(jieZhiTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return now.isAfter(timeFrame) ? 4 : 2;
        } else {
            LocalDateTime timeFrame = LocalDateTime.parse(jieZhiTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return now.isAfter(timeFrame) ? 3 : 2;
        }
    }
}
