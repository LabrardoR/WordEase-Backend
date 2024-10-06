package com.head.wordeasebackend.model.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.head.wordeasebackend.model.entity.SafetyUser;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserLoginResponse implements Serializable {

    private static final long serialVersionUID = 3191241726373120793L;

    /**
     * 用户信息
     */
    private SafetyUser user;

    /**
     * JWT 令牌
     */
    private String token;
}
