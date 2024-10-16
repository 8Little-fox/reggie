package com.itheima.reggie.common;

/**
 * 基于 ThreadLocal 封装工具类，用户保存和获取当前登录用户id
 *
 * ThreadLocal 为每一个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程
 * 内才能获取带对应的值，线程外则不能访问
 * ThreadLocal 提供 两个方法一个 get() ,一个set(), 可以通过set设置 getId,
 * 通过set) 获取
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
