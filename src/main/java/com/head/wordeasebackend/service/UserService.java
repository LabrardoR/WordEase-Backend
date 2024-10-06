package com.head.wordeasebackend.service;

import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSession;


/**
 * 用户服务
 *
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    Result userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param
     * @return 脱敏后的用户信息
     */
    Result userLogin(String userAccount, String userPassword);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    SafetyUser getSafetyUser(User originUser);

    /**
     *
     * @param token 令牌
     * @return 用户脱敏信息
     */
    SafetyUser getLoginUser(String token);

    boolean isLogin(String token);

    /**
     * 用户注销
     *
     * @param token
     * @return
     */
    int userLogout(String token);


}
