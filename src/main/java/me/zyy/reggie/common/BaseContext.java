package me.zyy.reggie.common;

/**
 *  基于 ThreadLocal 封装工具类，用以存取用户id，作用范围是当前线程之内!
 */

public class BaseContext {
    // 每一次请求 是一个新的一个线程 一个新的threadLocal对象
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
