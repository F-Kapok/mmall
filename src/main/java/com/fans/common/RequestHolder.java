package com.fans.common;

import com.fans.pojo.MmallUser;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName RequestHolder
 * @Description: TODO 多线程存储信息类 一般object为用户类 可自定已扩展
 * @Author fan
 * @Date 2018-11-08 13:20
 * @Version 1.0
 **/
public class RequestHolder {

    private static final ThreadLocal<MmallUser> objectHolder = new ThreadLocal<>();

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void add(MmallUser user) {
        objectHolder.set(user);
    }

    public static void add(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static Object getCurrentUser() {
        return objectHolder.get();
    }

    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }

    public static void remove() {
        objectHolder.remove();
        requestHolder.remove();
    }
}
