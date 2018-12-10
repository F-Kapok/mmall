package com.fans.controller.portal;

import com.fans.common.CommonConstants;
import com.fans.common.ResponseCode;
import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;
import com.fans.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @ClassName UserController
 * @Description: TODO 门户用户控制层
 * @Author fan
 * @Date 2018-11-23 11:49
 * @Version 1.0
 **/
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public ServerResponse<MmallUser> login(String username, String password, HttpSession session) {
        ServerResponse<MmallUser> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            session.setAttribute(CommonConstants.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    public ServerResponse<String> logout(HttpSession session) {
        session.removeAttribute(CommonConstants.CURRENT_USER);
        return ServerResponse.successMsg("Logout successfully");
    }

    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public ServerResponse<String> register(MmallUser user) {
        ServerResponse<String> result = iUserService.register(user);
        return result;
    }

    @RequestMapping(value = "/check_valid.do", method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str, String type) {
        ServerResponse<String> result = iUserService.checkValid(str, type);
        return result;
    }

    @RequestMapping(value = "/get_user_info.do", method = RequestMethod.POST)
    public ServerResponse<MmallUser> getUserInfo(HttpSession session) {
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.failureMsg("The user is not logger in and cannot get the current user information");
        }
        return ServerResponse.success(user);
    }

    @RequestMapping(value = "/forget_get_question.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse<String> result = iUserService.forgetGetQuestion(username);
        return result;
    }

    @RequestMapping(value = "/forget_check_answer.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        ServerResponse<String> result = iUserService.forgetCheckAnswer(username, question, answer);
        return result;
    }

    @RequestMapping(value = "/forget_reset_password.do", method = RequestMethod.POST)
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        ServerResponse<String> result = iUserService.forgetResetPassword(username, passwordNew, forgetToken);
        return result;
    }

    @RequestMapping(value = "/reset_password.do", method = RequestMethod.POST)
    private ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.failureMsg("The user is not logger in and cannot get current user information");
        }
        ServerResponse<String> result = iUserService.resetPassword(passwordOld, passwordNew, user.getId());
        return result;
    }

    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST)
    public ServerResponse<MmallUser> updateInformation(HttpSession session, MmallUser user) {
        MmallUser currentUser = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.failureMsg("The user is not logger in and cannot get current user information");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<MmallUser> result = iUserService.updateInformation(user);
        if (result.isSuccess()) {
            result.getData().setUsername(currentUser.getUsername());
            session.setAttribute(CommonConstants.CURRENT_USER, result.getData());
        }
        return result;
    }

    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    public ServerResponse<MmallUser> getInformation(HttpSession session) {
        MmallUser user = (MmallUser) session.getAttribute(CommonConstants.CURRENT_USER);
        if (user == null) {
            return ServerResponse.failureCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "The user is not logger in, You have to force login, statis = 10 ");
        }
        ServerResponse<MmallUser> result = iUserService.getInformation(user.getId());
        return result;
    }
}
