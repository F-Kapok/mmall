package com.fans.service.impl;

import com.fans.common.CommonConstants;
import com.fans.common.ServerResponse;
import com.fans.common.TokenCache;
import com.fans.dao.MmallUserMapper;
import com.fans.pojo.MmallUser;
import com.fans.pojo.MmallUserExample;
import com.fans.service.interfaces.IUserService;
import com.fans.utils.EncryptUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO 门户用户服务实现
 * @Author fan
 * @Date 2018-11-23 11:58
 * @Version 1.0
 **/
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private MmallUserMapper userMapper;

    @Override
    public ServerResponse<MmallUser> login(String username, String password) {
        int isExist = userMapper.checkUserByUsername(username);
        if (isExist == 0)
            return ServerResponse.failureMsg("The user is not exist");
        String md5Password = EncryptUtils.MD5Encrypt(password, "UTF-8");
        MmallUser user = userMapper.login(username, md5Password);
        if (user == null)
            return ServerResponse.failureMsg("Login failure,Please check the password");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.success("Login successfully", user);
    }

    @Override
    public ServerResponse<String> register(MmallUser user) {
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), CommonConstants.USERNAME);
        if (!validResponse.isSuccess())
            return validResponse;
        validResponse = this.checkValid(user.getEmail(), CommonConstants.EMAIL);
        if (!validResponse.isSuccess())
            return validResponse;
        user.setPassword(EncryptUtils.MD5Encrypt(user.getPassword(), "UTF-8"));
        user.setRole(CommonConstants.Role.ROLE_CUSTOMER);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        int count = userMapper.insertSelective(user);
        if (count == 0)
            return ServerResponse.failureMsg("Fail to register");
        return ServerResponse.successMsg("Registered successfully");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        int isExist;
        if (StringUtils.isNotBlank(type)) {
            if (CommonConstants.USERNAME.equals(type)) {
                isExist = userMapper.checkUserByUsername(str);
                if (isExist > 0)
                    return ServerResponse.failureMsg("The user already exists");
            } else if (CommonConstants.EMAIL.equals(type)) {
                isExist = userMapper.checkUserByEmail(str);
                if (isExist > 0)
                    return ServerResponse.failureMsg("The email already exists");
            }
        } else {
            return ServerResponse.failureMsg("The valid type is blank");
        }
        return ServerResponse.successMsg("Check success");
    }

    @Override
    public ServerResponse<String> forgetGetQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, CommonConstants.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.failureMsg("The user does not exist");
        }
        MmallUserExample userExample = new MmallUserExample();
        MmallUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        String questionStr = userMapper.selectByExample(userExample).get(0).getQuestion();
        if (StringUtils.isNotBlank(questionStr))
            return ServerResponse.success(questionStr);
        return ServerResponse.failureMsg("The user questions is empty");
    }

    @Override
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        MmallUserExample userExample = new MmallUserExample();
        MmallUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andUsernameEqualTo(username);
        criteria.andQuestionEqualTo(question);
        criteria.andAnswerEqualTo(answer);
        int result = userMapper.selectByExample(userExample).size();
        if (result == 0)
            return ServerResponse.failureMsg("The answer is wrong");
        String token = UUID.randomUUID().toString();
        TokenCache.setLoadingCache(TokenCache.TOKEN_PREFIX + username, token);
        return ServerResponse.success(token);
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String token) {
        if (StringUtils.isBlank(token))
            return ServerResponse.failureMsg("The token cannot be empty");
        ServerResponse validResponse = this.checkValid(username, CommonConstants.USERNAME);
        if (validResponse.isSuccess()) {
            return ServerResponse.failureMsg("The user does not exist");
        }
        String localToken = TokenCache.getLoadingCache(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(localToken))
            return ServerResponse.failureMsg("The token has expired");
        if (StringUtils.equals(token, localToken)) {
            String password = EncryptUtils.MD5Encrypt(passwordNew, "utf-8");
            MmallUser user = MmallUser.builder()
                    .password(password)
                    .updateTime(new Date())
                    .build();
            MmallUserExample userExample = new MmallUserExample();
            MmallUserExample.Criteria criteria = userExample.createCriteria();
            criteria.andUsernameEqualTo(username);
            int count = userMapper.updateByExampleSelective(user, userExample);
            if (count == 0)
                return ServerResponse.failureMsg("The password update failure");
            return ServerResponse.successMsg("The password update successfully");
        }
        return ServerResponse.failureMsg("The password update failure");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, Integer userId) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要指定是这个用户.因为我们会查询一个count(1),如果不指定id,那么结果就是true啦count>0;
        MmallUserExample userExample = new MmallUserExample();
        MmallUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andIdEqualTo(userId);
        criteria.andPasswordEqualTo(EncryptUtils.MD5Encrypt(passwordOld, "utf-8"));
        int isExist = userMapper.selectByExample(userExample).size();
        if (isExist == 0)
            return ServerResponse.failureMsg("The old password is wrong");
        MmallUser user = MmallUser.builder()
                .id(userId)
                .password(EncryptUtils.MD5Encrypt(passwordNew, "utf-8"))
                .updateTime(new Date())
                .build();
        int result = userMapper.updateByPrimaryKeySelective(user);
        if (result > 0)
            return ServerResponse.successMsg("The password update successfully");
        return ServerResponse.failureMsg("The password update failure");
    }

    @Override
    public ServerResponse<MmallUser> updateInformation(MmallUser user) {
        //username是不能被更新的
        //email也要进行一个校验,校验新的email是不是已经存在,并且存在的email如果相同的话,不能是我们当前的这个用户的
        MmallUserExample userExample = new MmallUserExample();
        MmallUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andEmailEqualTo(user.getEmail());
        criteria.andIdNotEqualTo(user.getId());
        int count = userMapper.selectByExample(userExample).size();
        if (count > 0)
            return ServerResponse.failureMsg("The email already exists");
        MmallUser updatedUser = MmallUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .question(user.getQuestion())
                .answer(user.getAnswer())
                .updateTime(new Date())
                .build();
        int result = userMapper.updateByPrimaryKeySelective(updatedUser);
        if (result > 0)
            return ServerResponse.success("The user information update successfully", updatedUser);
        return ServerResponse.failureMsg("The user information update failure");
    }

    @Override
    public ServerResponse<MmallUser> getInformation(Integer userId) {
        MmallUser user = userMapper.selectByPrimaryKey(userId);
        if (user == null)
            return ServerResponse.failureMsg("The current user could not be found");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.success(user);
    }
}
