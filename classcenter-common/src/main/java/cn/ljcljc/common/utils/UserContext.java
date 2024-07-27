package cn.ljcljc.common.utils;

import cn.ljcljc.common.domain.pojo.UserInfo;

public class UserContext {
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    /**
     * 保存当前登录用户信息到ThreadLocal
     * @param userInfo 用户西门学哦
     */
    public static void setUser(UserInfo userInfo) {
        tl.set(userInfo);
    }

    /**
     * 获取当前登录用户信息
     * @return 用户id
     */
    public static UserInfo getUser() {
        return tl.get();
    }

    /**
     * 移除当前登录用户信息
     */
    public static void removeUser(){
        tl.remove();
    }
}
