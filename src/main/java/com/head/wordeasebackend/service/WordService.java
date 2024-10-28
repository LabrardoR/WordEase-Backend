package com.head.wordeasebackend.service;

import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.response.WordQueryResponse;
import com.head.wordeasebackend.model.response.WordQueryResponse;
import com.head.wordeasebackend.model.entity.Word;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
* @author headhead
* @description 针对表【word(常见单词词库)】的数据库操作Service
* @createDate 2024-09-14 21:53:38
*/
public interface WordService extends IService<Word> {

    WordQueryResponse queryWordBySpelling(String wordSpelling);

    SseEmitter queryWordBySpellingByAI(String wordSpelling);

    SseEmitter querySentenceByAI(String sentence);

    SseEmitter exerciseWords(List<String> wordList);

    Result checkAnswer(List<String> answerList);
}
