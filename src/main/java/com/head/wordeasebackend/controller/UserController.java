package com.head.wordeasebackend.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.head.wordeasebackend.common.ErrorCode;
import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.exception.BusinessException;
import com.head.wordeasebackend.model.entity.User;
import com.head.wordeasebackend.model.request.UserLoginRequest;
import com.head.wordeasebackend.model.request.UserRegisterRequest;
import com.head.wordeasebackend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;
import static com.head.wordeasebackend.contant.UserConstant.ADMIN_ROLE;
import static com.head.wordeasebackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 */

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
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
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return Result.fail("登录信息为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return Result.fail("账号或密码为空");
        }
        Result r = userService.userLogin(userAccount, userPassword, request);
        User user = (User) r.getData();
        if(user == null){
            return Result.fail("账号或密码错误");
        }
        return Result.ok(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        if(result != 1){
            return Result.fail("注销失败！");
        }
        return Result.ok("退出成功！");
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public Result getCurrentUser(HttpServletRequest request) {
        User safetyUser = userService.getLoginUser(request);
        return Result.ok(safetyUser);
    }

    // todo 管理员查询用户列表
    @GetMapping("/search")
    public Result searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return Result.ok(list);
    }

    @PostMapping("/delete")
    public Result deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return Result.ok(b);
    }


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null;
    }

}
