package com.head.wordeasebackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.model.dto.WordDto;
import com.head.wordeasebackend.model.entity.Word;
import com.head.wordeasebackend.service.WordService;
import com.head.wordeasebackend.mapper.WordMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    public WordDto queryWordBySpelling(String wordSpelling) {

        QueryWrapper<Word> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spelling",wordSpelling);
        List<Word> wordList = wordMapper.selectList(queryWrapper);
        if(wordList.isEmpty()){
            return null;
        }
        Word word = wordList.get(0);
        WordDto wordDto = new WordDto();
        wordDto.setSpelling(word.getSpelling());
        wordDto.setDefinition(word.getDefinition());
        wordDto.setPhonetic(word.getPhonetic());
        wordDto.setExampleSentence(word.getExampleSentence());
        if(wordList.size() == 2){
            wordDto.setWordType("CET4, CET6");
        } else if(word.getWordType() == 1){
            wordDto.setWordType("CET-4");
        } else if(word.getWordType() == 2){
            wordDto.setWordType("CET-6");
        }

        return wordDto;
    }
}




