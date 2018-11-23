package com.fans.service.interfaces;

import com.fans.common.ServerResponse;
import com.fans.pojo.MmallUser;

/**
 * @InterfaceName IUserService
 * @Description: TODO 门户用户服务接口
 * @Author fan
 * @Date 2018-11-23 11:50
 * @Version 1.0
 **/
public interface IUserService {

    ServerResponse<MmallUser> login(String username, String password);

    ServerResponse register(MmallUser user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> forgetGetQuestion(String username);

    ServerResponse<String> forgetCheckAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String token);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, Integer userId);

    ServerResponse<MmallUser> updateInformation(MmallUser user);

    ServerResponse<MmallUser> getInformation(Integer userId);
}
