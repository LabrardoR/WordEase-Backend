package com.head.wordeasebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.model.entity.User;
import com.head.wordeasebackend.model.response.UserLoginResponse;
import com.head.wordeasebackend.service.UserService;
import com.head.wordeasebackend.mapper.UserMapper;
import com.head.wordeasebackend.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.head.wordeasebackend.contant.RedisConstant.USER_LOGOUT_TOKEN;

/**
 * 用户服务实现类
 *
 * @author 
 * @from 
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private UserMapper userMapper;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "head";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public Result userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            Result.fail("账号或密码为空");
        }
        if (userAccount.length() < 4) {
            return Result.fail("账号长度不能小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return Result.fail("密码长度不能小于8位");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return Result.fail("账号不能包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return Result.fail("两次输入的密码不同");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            return Result.fail("账号重复");
        }
 
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return Result.fail("注册失败，数据库错误");
        }
        return Result.ok(user.getId());
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    @Override
    public Result userLogin(String userAccount, String userPassword) {
        // 1. 校验
        if (StrUtil.isBlank(userAccount) || StrUtil.isBlank(userPassword)) {
            return Result.fail("账号或密码为空");
        }
        if (userAccount.length() < 4) {
            return Result.fail("账号长度不能小于4位");
        }
        if (userPassword.length() < 8) {
            return Result.fail("密码长度不能小于8位");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return Result.fail("账号不能包含特殊字符");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("用户名与密码不匹配");
            return Result.fail("用户不存在");
        }
        // 3. 用户脱敏
        SafetyUser safetyUser = getSafetyUser(user);
        // 4. 生成JWT令牌
        String token = JwtUtil.genToken(safetyUser);
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setUser(safetyUser);
        userLoginResponse.setToken(token);

        return Result.ok(userLoginResponse);
    }

    /**
     * 用户脱敏
     *
     * @param originUser 原始信息
     * @return 脱敏后的信息
     */
    @Override
    public SafetyUser getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        SafetyUser safetyUser = new SafetyUser();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setRole(originUser.getRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    // todo
    @Override
    public SafetyUser getLoginUser(String token) {
        JwtUtil jwtUtil = new JwtUtil(stringRedisTemplate);
        if(jwtUtil.isInBlackList(token)){
            return null;
        }
        SafetyUser safetyUser = jwtUtil.parseToken(token);
        return safetyUser;
    }

    /**
     * 获取当前登录用户 todo
     * @param
     * @return 脱敏后的用户登录信息，如果未登录则返回 null
     */

//    @Override
//    public SafetyUser getLoginUser(HttpSession session) {
//        Object userObj = session.getAttribute(USER_LOGIN_STATE);
//        User currentUser = (User) userObj;
//        if (currentUser == null) {
//            log.info("未登录");
//            return null;
//        }
//        long userId = currentUser.getId();
//        // TODO 校验用户是否合法
//        User user = this.getById(userId);
//        if(user == null){
//            log.info("用户不存在");
//            return null;
//        }
//        SafetyUser safetyUser = this.getSafetyUser(user);
//        return safetyUser;
//    }

    @Override
    public boolean isLogin(String token) {
        return getLoginUser(token) != null;
    }

    /**
     * 用户注销
     * 注销逻辑：在redis中建立专属黑名单，若用户注销，则将当前用户的token存入黑名单中。
     * 若用户调用需要鉴权的接口，则判断token是否在黑名单中，若在，则拒绝访问。
     * @param token 令牌
     */
    @Override
    public int userLogout(String token) {
        String logoutKey = USER_LOGOUT_TOKEN + token;
        // 移除登录态
        stringRedisTemplate.opsForValue().set(logoutKey,"invalid",1, TimeUnit.DAYS);
        return 1;
    }




}
