package com.head.wordeasebackend.model.request;

import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.model.entity.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 3191245516373120793L;

    private SafetyUser safetyUser;

    private String token;
}
