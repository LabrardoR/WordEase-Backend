package com.head.wordeasebackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.head.wordeasebackend.model.entity.SafetyUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

import static com.head.wordeasebackend.contant.RedisConstant.USER_LOGOUT_TOKEN;

@Slf4j
@Component
public class JwtUtil {

    @Resource
    private final StringRedisTemplate stringRedisTemplate;

    // 配置签名
    private static final String KEY = "@#$%^&*()1245!!!";

    public JwtUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    //接收业务数据,生成token并返回
    public static String genToken(SafetyUser safetyUser) {
        return JWT.create()
                .withIssuedAt(new Date())//添加签发时间
                .withClaim("id", safetyUser.getId())//添加载荷
                .withClaim("userAccount", safetyUser.getUserAccount())
                .withClaim("username", safetyUser.getUsername())
                .withClaim("gender", safetyUser.getGender())
                .withClaim("avatarUrl", safetyUser.getAvatarUrl())
                .withClaim("email", safetyUser.getEmail())
                .withClaim("role", safetyUser.getRole())
                .withClaim("createTime", safetyUser.getCreateTime())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000*60*60*24))//添加过期时间一天
                .sign(Algorithm.HMAC256(KEY)); // 使用HMAC256算法签名
    }

    public boolean isInBlackList(String token){
        // 检查 token 是否在黑名单中
        String blackListToken = USER_LOGOUT_TOKEN + token;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(blackListToken));
    }
    public SafetyUser parseToken(String token) {
        // 检查 token 是否在黑名单中
        if (isInBlackList(token)) {
            return null;
        }
        SafetyUser safetyUser = new SafetyUser();
        try {
            // 使用密钥和算法验证 Token
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(KEY))
                    .build()
                    .verify(token);

            // 设置 User 实体的字段
            safetyUser.setId(decodedJWT.getClaim("id").asLong());
            safetyUser.setUserAccount(decodedJWT.getClaim("userAccount").asString());
            safetyUser.setUsername(decodedJWT.getClaim("username").asString());
            safetyUser.setAvatarUrl(decodedJWT.getClaim("avatarUrl").asString());
            safetyUser.setGender(decodedJWT.getClaim("gender").asInt());
            safetyUser.setEmail(decodedJWT.getClaim("email").asString());
            safetyUser.setCreateTime(decodedJWT.getClaim("createTime").asDate());
            safetyUser.setRole(decodedJWT.getClaim("role").asInt());
            // 其他不需要从 token 中解析的字段可以默认不设置
        } catch (JWTVerificationException e) {
            // 处理token校验失败的情况，比如token过期、签名不合法等
            log.info("token校验失败");
            return null;
        }
        return safetyUser;
    }

}

