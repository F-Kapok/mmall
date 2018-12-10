package com.fans.controller.backend;

import com.fans.common.CommonConstants;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;
import com.fans.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @ClassName UserManagerController
 * @Description: TODO 管理系统用户控制层
 * @Author fan
 * @Date 2018-11-23 16:06
 * @Version 1.0
 **/
@RestController
@RequestMapping("/manage/user")
public class UserManagerController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public ServerResponse<MmallUser> login(String username, String password, HttpSession session) {
        ServerResponse<MmallUser> result = iUserService.login(username, password);
        if (result.isSuccess()) {
            MmallUser user = result.getData();
            if (user.getRole().equals(CommonConstants.Role.ROLE_ADMIN)) {
                //说明登录的是管理员
                session.setAttribute(CommonConstants.CURRENT_USER, user);
                return result;
            } else {
                return ServerResponse.failureMsg("The user is not an administrator and cannot log in");
            }
        }
        return result;
    }
}
