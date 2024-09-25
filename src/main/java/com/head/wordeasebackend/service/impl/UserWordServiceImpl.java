package com.head.wordeasebackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.mapper.WordMapper;
import com.head.wordeasebackend.model.dto.WordToListRequest;
import com.head.wordeasebackend.model.entity.User;
import com.head.wordeasebackend.model.entity.UserWord;
import com.head.wordeasebackend.model.entity.Word;
import com.head.wordeasebackend.service.UserService;
import com.head.wordeasebackend.service.UserWordService;
import com.head.wordeasebackend.mapper.UserWordMapper;
import com.head.wordeasebackend.service.WordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author headhead
* @description 针对表【user_word(用户单词关联表)】的数据库操作Service实现
* @createDate 2024-09-14 21:53:38
*/
@Service
@Slf4j
public class UserWordServiceImpl extends ServiceImpl<UserWordMapper, UserWord>
    implements UserWordService{

    @Resource
    private UserService userService;
    @Resource
    private WordService wordService;
    @Resource
    private UserWordMapper userWordMapper;
    @Resource
    private WordMapper wordMapper;

    @Override
    public Long addWordToUserWordList(User user, WordToListRequest wordToListRequest) {
        if(wordToListRequest == null){
            return null;
        }

        Date date = wordToListRequest.getDate();
        String wordSpelling = wordToListRequest.getWordSpelling();
        Integer proficiency = wordToListRequest.getProficiency();
        // 查询单词id
        QueryWrapper<Word> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spelling",wordSpelling);
        Word word = wordMapper.selectOne(queryWrapper);
        Long wordId =  word.getId();
        Long userId = user.getId();
        String spelling = word.getSpelling();
        log.info("wordId:" + wordId);

        UserWord userWord = new UserWord();
        userWord.setUserId(userId);
        userWord.setWordId(wordId);
        userWord.setSpelling(spelling);
        userWord.setProficiency(proficiency);
        userWord.setAttribute(1);
        boolean result = save(userWord);

        return result ? userWord.getId() : null;
    }

    @Override
    public List<WordToListRequest> getWordList(User user) {
        // 1. 在 user_word 表中查询用户的单词表
        QueryWrapper<UserWord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user.getId());
        List<UserWord> userWordList = list(queryWrapper);

        // 2. 将 userWordList 转换为 wordIdList
        List<Long> userWordIdList = userWordList.stream().map(UserWord::getWordId).collect(Collectors.toList());

        System.out.println(userWordIdList);

        // 3. 将 UserWord 封装为 WordToListRequest
        List<WordToListRequest> wordToListRequestList = userWordList.stream().map(userWord -> {
            WordToListRequest wordToListRequest = new WordToListRequest();
            wordToListRequest.setWordSpelling(userWord.getSpelling());
            wordToListRequest.setProficiency(userWord.getProficiency());
            wordToListRequest.setDate(userWord.getCreateTime());
            return wordToListRequest;
        }).collect(Collectors.toList());

        return wordToListRequestList;
    }
}




