package cn.ljcljc.common.utils;

import java.util.Arrays;
import java.util.List;

public class Constant {
    /**
     * 用户session名称
     */
    public static final String USER_SESSION_NAME = "currentUser";

    /**
     * 用户初始密码
     */
    public static final String USER_INIT_PASSWORD = "024a11a394466247356c616151b0ad37";

    /**
     * 用户权限分组字段
     */
    public static final String PERMISSION_GROUP_SUPER_ADMIN = "1"; // 超级管理员
    public static final String PERMISSION_GROUP_ADMIN = "2"; // 管理员
    public static final String PERMISSION_GROUP_USER = "3"; // 普通用户
    public static final String PERMISSION_GROUP_RP = "4"; // 科代表

    /**
     * 登录秘钥
     */
    public static final String USER_LOGIN_SECRET_KEY = "_ljcsys";

    /**
     * 登记表收集表类型数数字
     * 登记表1，收集表2
     */
    public static final String USER_TASK_REGISTER_TYPE = "1";
    public static final String USER_TASK_COLLECT_TYPE = "2";

    /**
     * 团员政治面貌
     */
    public static final List<String> USER_ZZMM = Arrays.asList("共青团员", "中共预备党员", "中共党员");

    /**
     * 班委职位ID
     */
    public static final Integer USER_BANWEI_BANZHANG = 1;
    public static final Integer USER_BANWEI_FUBANZHANG = 2;
    public static final Integer USER_BANWEI_TUANZHISHU = 3;
    public static final Integer USER_BANWEI_XUEXIWEIYUAN = 4;
    public static final Integer USER_BANWEI_ZUZHIWEIYUAN = 5;
    public static final Integer USER_BANWEI_WENTIWEIYUAN = 6;
    public static final Integer USER_BANWEI_SHENGLAOWEIYUAN = 7;
    public static final Integer USER_BANWEI_JILVYUAN = 8;

    /**
     * 文件后缀
     */
    public static final String WORD_FILE_SUFFIX = " .doc, .docx,";
    public static final String EXCEL_FILE_SUFFIX = " .xls, .xlsx,";
    public static final String POWERPOINT_FILE_SUFFIX = " .ppt, .pptx,";
    public static final String PDF_FILE_SUFFIX = " .pdf,";
    public static final String PNG_FILE_SUFFIX = " .png, .jpg, .jpeg,";
    public static final String ZIP_FILE_SUFFIX = " .zip, .rar, .7z, .tar.gz,";

    /**
     * 收集表审核状态字段
     */
    public static final String USER_TASK_COLLECT_SHENHE_ACCEPT = "1";
    public static final String USER_TASK_COLLECT_SHENHE_REJECT = "2";

    /**
     * 素拓申报审核状态字段
     */
    public static final String USER_SUTUO_SHENHE_WAITING = "1";
    public static final String USER_SUTUO_SHENHE_ACCEPT = "2";
    public static final String USER_SUTUO_SHENHE_REJEFCT = "3";

    /**
     * QQ登录认证接口
     */
    public static final String QQ_AUTHORIZE_URL = "https://graph.qq.com/oauth2.0/authorize";

    /**
     * QQ认证服务器申请令牌接口
     */
    public static final String QQ_AUTHORIZE_TOKEN_URL = "https://graph.qq.com/oauth2.0/token";

    /**
     * 获取QQ用户的openId接口
     */
    public static final String QQ_AUTHORIZE_OPENID_URL = "https://graph.qq.com/oauth2.0/me";

    /**
     * 微信小程序配置
     */
    public static final String WECHAT_APP_ID = "YOUR_WECHAT_APP_ID";
    public static final String WECHAT_APP_SECRET = "WECHAT_APP_SECRET";
    public static final String WECHAT_RESPONSE_TOKEN_NAME = "access_token";

    /**
     * 微信小程序登录 accessToken缓存名称
     */
    public static final String WECHAT_ACCESS_TOKEN = "wx_access_token";


}
