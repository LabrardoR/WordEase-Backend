package com.head.wordeasebackend.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.model.entity.User;
import com.head.wordeasebackend.model.request.TokenRequest;
import com.head.wordeasebackend.model.request.UserLoginRequest;
import com.head.wordeasebackend.model.request.UserRegisterRequest;
import com.head.wordeasebackend.model.request.UserUpdateRequest;
import com.head.wordeasebackend.service.UserService;
import com.head.wordeasebackend.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 用户接口
 *
 */

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求体
     * @return 注册结果(注册id)
     */
    @PostMapping("/register")
    public Result userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            Result.fail("请求参数为空");
        }
        assert userRegisterRequest != null;
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StrUtil.isBlank(userAccount) || StrUtil.isBlank(userPassword) || StrUtil.isBlank(checkPassword)) {
            Result.fail("账号或密码为空");
        }
        Result r = userService.userRegister(userAccount, userPassword, checkPassword);
        if(r == null){
            return Result.fail("注册失败");
        }
        return Result.ok(r);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest 登录请求体
     * @param
     * @return
     */
    @PostMapping("/login")
    public Result userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            return Result.fail("登录信息为空");
        }
        String token = userLoginRequest.getToken();
        JwtUtil jwtUtil = new JwtUtil(stringRedisTemplate);
        if (StringUtils.isNotBlank(token) && !jwtUtil.isInBlackList(token)) {
            // 验证token是否合法
            SafetyUser safetyUser = jwtUtil.parseToken(token);
            if (safetyUser == null) {
                return Result.fail("token不合法");
            }
            return Result.ok("已登录");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return Result.fail("账号或密码为空");
        }
        Result r = userService.userLogin(userAccount, userPassword);
        if(r == null) {
            return Result.fail("登录失败");
        }
        return r;
    }

    /**
     * 短信验证码登录 todo
     * @param userLoginRequest
     * @return 结果
     */
    @PostMapping("/codeLogin")
    public Result userLoginByCode(@RequestBody UserLoginRequest userLoginRequest) {
        return Result.fail("功能未完成！");
//        if (userLoginRequest == null) {
//            return Result.fail("登录信息为空");
//        }
//        String token = userLoginRequest.getToken();
//        JwtUtil jwtUtil = new JwtUtil(stringRedisTemplate);
//        if (StringUtils.isNotBlank(token) && !jwtUtil.isInBlackList(token)) {
//            // 验证token是否合法
//            SafetyUser safetyUser = jwtUtil.parseToken(token);
//            if (safetyUser == null) {
//                return Result.fail("token不合法");
//            }
//            return Result.ok("已登录");
//        }
//        String userAccount = userLoginRequest.getUserAccount();
//        String userPassword = userLoginRequest.getUserPassword();
//        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
//            return Result.fail("账号或密码为空");
//        }
//        Result r = userService.userLogin(userAccount, userPassword);
//        if(r == null) {
//            return Result.fail("登录失败");
//        }
//        return r;
    }


    /**
     * 用户注销
     * @param tokenRequest Token封装类
     * @return 成功--1
     */
    @PostMapping("/logout")
    public Result userLogout(@RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();
        if (token == null) {
            Result.fail("参数错误");
        }
        int result = userService.userLogout(token);
        if(result != 1){
            return Result.fail("退出失败！");
        }
        return Result.ok("退出成功！");
    }

    @PostMapping("/update")
    public Result updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        SafetyUser safetyUser = userUpdateRequest.getSafetyUser();
        String token = userUpdateRequest.getToken();
        if (token == null || safetyUser == null) {
            Result.fail("参数错误");
        }
        JwtUtil jwtUtil = new JwtUtil(stringRedisTemplate);
        SafetyUser loginUser = jwtUtil.parseToken(token);
        if(loginUser == null){
            return Result.fail("token不合法");
        }

        User user = safetyUser.toUser();
        if (user == null) {
            Result.fail("参数错误");
        }
        // 如果不是管理员，则不允许更改其他人的信息
        if(!Objects.equals(loginUser.getId(), safetyUser.getId()) && loginUser.getRole()!= 1){
            return Result.fail("无权限");
        }

        boolean result = userService.updateById(user);
        if(!result){
            return Result.fail("更新失败！");
        }
        return Result.ok("更新成功！");
    }

    /**
     * 获取当前用户
     *
     * @param tokenRequest 令牌
     * @return
     */
    @PostMapping("/current")
    public Result getCurrentUser(@RequestBody TokenRequest tokenRequest){
        String token = tokenRequest.getToken();
        if (token == null) {
            Result.fail("参数错误");
        }
        SafetyUser safetyUser = userService.getLoginUser(token);
        if(safetyUser == null){
            return Result.fail("用户未登录");
        }
        return Result.ok(safetyUser);
    }





    // todo 管理员查询用户列表

    @PostMapping("/search")
    public Result searchUsers(@RequestBody TokenRequest tokenRequest) {
        if(tokenRequest == null){
            Result.fail("参数错误");
        }
        String token = tokenRequest.getToken();
        if(token == null){
            Result.fail("token为空");
        }

        JwtUtil jwtUtil = new JwtUtil(stringRedisTemplate);
        SafetyUser safetyUser = jwtUtil.parseToken(token);
        if(safetyUser == null){
            return Result.fail("token不合法");
        }

        if (safetyUser.getRole() != 1) {
            Result.fail("无管理员权限");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role",0);
        List<User> userList = userService.list(queryWrapper);
        List<SafetyUser> list = userList.stream().map(user1 -> userService.getSafetyUser(user1)).collect(Collectors.toList());
        return Result.ok(list);
    }

//    @PostMapping("/delete")
//    public Result deleteUser(@RequestBody long id, HttpSession session) {
//        if (!isAdmin(session)) {
//            throw new BusinessException(ErrorCode.NO_AUTH);
//        }
//        if (id <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        boolean b = userService.removeById(id);
//        return Result.ok(b);
//    }



}
