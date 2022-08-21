package com.martin.reggie.common;

/**
 * 工具类：用于存取当前登录用户id，而不需要HttpServletRequest
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
