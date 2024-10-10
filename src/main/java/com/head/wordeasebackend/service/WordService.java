package com.head.wordeasebackend.service;

import com.head.wordeasebackend.model.response.WordSearchResponse;
import com.head.wordeasebackend.model.entity.Word;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author headhead
* @description 针对表【word(常见单词词库)】的数据库操作Service
* @createDate 2024-09-14 21:53:38
*/
public interface WordService extends IService<Word> {

    WordSearchResponse queryWordBySpelling(String wordSpelling);
}
