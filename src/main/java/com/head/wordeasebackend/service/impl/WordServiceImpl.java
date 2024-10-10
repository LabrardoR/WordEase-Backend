package com.head.wordeasebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.model.response.WordSearchResponse;
import com.head.wordeasebackend.model.entity.Word;
import com.head.wordeasebackend.service.WordService;
import com.head.wordeasebackend.mapper.WordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author headhead
* @description 针对表【word(常见单词词库)】的数据库操作Service实现
* @createDate 2024-09-14 21:53:38
*/
@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word>
    implements WordService{

    @Resource
    private WordMapper wordMapper;

    @Override
    public WordSearchResponse queryWordBySpelling(String wordSpelling) {

        QueryWrapper<Word> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spelling",wordSpelling);
        Word word = wordMapper.selectOne(queryWrapper);

        if(word == null){
            return null;
        }
        WordSearchResponse wordSearchResponse = new WordSearchResponse();
        wordSearchResponse.setSpelling(word.getSpelling());
        wordSearchResponse.setDefinition(word.getDefinition());
        wordSearchResponse.setPhonetic(word.getPhonetic());
        wordSearchResponse.setExampleSentence(word.getExampleSentence());
        if(word.getWordType() == 3){
            wordSearchResponse.setWordType("CET4, CET6");
        } else if(word.getWordType() == 1){
            wordSearchResponse.setWordType("CET-4");
        } else if(word.getWordType() == 2){
            wordSearchResponse.setWordType("CET-6");
        }

        return wordSearchResponse;
    }
}




