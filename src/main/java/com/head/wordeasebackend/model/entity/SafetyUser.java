package com.head.wordeasebackend.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 去除敏感信息后的用户信息
 */
@Data
public class SafetyUser implements Serializable{

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setUserAccount(userAccount);
        user.setAvatarUrl(avatarUrl);
        user.setGender(gender);
        user.setEmail(email);
        user.setCreateTime(createTime);
        return user;
    }
}
