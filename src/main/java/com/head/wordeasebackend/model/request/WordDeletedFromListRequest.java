package com.head.wordeasebackend.model.request;

import lombok.Data;

@Data
public class WordDeletedFromListRequest {

    private String token;       // 令牌

    private String wordSpelling; // 单词拼写
}
