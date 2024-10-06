package com.head.wordeasebackend.controller;

import cn.hutool.core.util.StrUtil;
import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.model.request.TokenRequest;
import com.head.wordeasebackend.model.request.UserLoginRequest;
import com.head.wordeasebackend.model.request.UserRegisterRequest;
import com.head.wordeasebackend.service.UserService;
import com.head.wordeasebackend.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


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
        if (token != null && jwtUtil.isValidToken(token)) {
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

    /**
     * 获取当前用户
     *
     * @param tokenRequest 令牌
     * @return
     */
    @GetMapping("/current")
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

//    // todo 管理员查询用户列表
//    @GetMapping("/search")
//    public Result searchUsers(String username) {
//        if (!isAdmin(session)) {
//            Result.fail("无管理员权限");
//        }
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        if (StringUtils.isNotBlank(username)) {
//            queryWrapper.like("username", username);
//        }
//        List<User> userList = userService.list(queryWrapper);
//        List<SafetyUser> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
//        return Result.ok(list);
//    }

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


//    /**
//     * 是否为管理员
//     *
//     * @param
//     * @return
//     */
//    private boolean isAdmin(HttpSession session) {
//        // 仅管理员可查询
//        Object userObj = session.getAttribute(USER_LOGIN_STATE);
//        User user = (User) userObj;
//        return user != null;
//    }

}
