package com.fans.common;

import com.fans.pojo.MmallUser;
import com.fans.service.interfaces.IUserService;
import com.fans.utils.ApplicationContextHelper;

/**
 * @ClassName MmallCommon
 * @Description:  通用方法
 * @Author fan
 * @Date 2018-12-10 17:38
 * @Version 1.0
 **/
public class MmallCommon {
    public static ServerResponse checkUser() {
        IUserService iUserService = ApplicationContextHelper.popBean("iUserService", IUserService.class);
        MmallUser user = (MmallUser) RequestHolder.getCurrentUser();
        if (user == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        } else {
            if (!iUserService.checkAdminRole(user).isSuccess()) {
                return ServerResponse.failureMsg("无权限操作，需要管理员权限");
            }
            return ServerResponse.success();
        }
    }
}
