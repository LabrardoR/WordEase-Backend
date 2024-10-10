package com.head.wordeasebackend.model.request;

import lombok.Data;

import java.util.Date;

/**
 * 单词添加请求封装类(用户添加单词到单词表)
 */
@Data
public class WordToListRequest {
    private Date date;           // 添加日期
    private String wordSpelling; // 单词拼写
    private Integer proficiency; // 熟练度
    private String token;       // 令牌
}
