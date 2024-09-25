package com.head.wordeasebackend.service;

import com.head.wordeasebackend.model.dto.WordToListRequest;
import com.head.wordeasebackend.model.entity.User;
import com.head.wordeasebackend.model.entity.UserWord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author headhead
* @description 针对表【user_word(用户单词关联表)】的数据库操作Service
* @createDate 2024-09-14 21:53:38
*/
public interface UserWordService extends IService<UserWord> {

    Long addWordToUserWordList(User user, WordToListRequest wordToListRequest);

    List<WordToListRequest> getWordList(User user);
}
