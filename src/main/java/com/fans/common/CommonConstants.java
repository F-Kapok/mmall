package com.fans.common;

/**
 * @ClassName CommonConstants
 * @Description:  共用Key枚举
 * @Author fan
 * @Date 2018-11-23 11:23
 * @Version 1.0
 **/

public class CommonConstants {
    public static final String CURRENT_USER = "currentUser";
    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface Role {
        Integer ROLE_CUSTOMER = 0; //普通用户
        Integer ROLE_ADMIN = 1;//管理员
    }
}
