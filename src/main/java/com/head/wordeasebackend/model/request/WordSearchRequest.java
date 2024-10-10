package com.head.wordeasebackend.model.request;

import lombok.Data;

/**
 * 单词释义请求封装类
 */
@Data
public class WordSearchRequest {
    private String wordSpelling;
}
