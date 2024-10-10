package com.head.wordeasebackend.model.response;

import lombok.Data;

/**
 *   单词信息封装类
 */
@Data
public class WordSearchResponse {
    /**
     * 单词拼写
     */
    private String spelling;

    /**
     * 单词音标
     */
    private String phonetic;

    /**
     * 单词释义
     */
    private String definition;

    /**
     * 单词例句
     */
    private String exampleSentence;

    /**
     * 单词类型
     */
    private String wordType;

}
