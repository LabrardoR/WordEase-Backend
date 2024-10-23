package com.head.wordeasebackend.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.model.response.WordQueryResponse;
import com.head.wordeasebackend.model.entity.Word;
import com.head.wordeasebackend.service.WordService;
import com.head.wordeasebackend.mapper.WordMapper;
import com.head.wordeasebackend.utils.BigModelUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

import java.util.List;

import static com.head.wordeasebackend.contant.RedisConstant.WORD_DATA;

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

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private BigModelUtil bigModelUtil;



    @Override
    public WordQueryResponse queryWordBySpelling(String wordSpelling) {
        String key = WORD_DATA + wordSpelling;

        // 先去 Redis 中查询
        String wordJson = stringRedisTemplate.opsForValue().get(key);

        if(wordJson != null){
            // 存在，直接返回
            JSONObject jsonObject = new JSONObject(wordJson);
            Word word = jsonObject.toBean(Word.class);
            // 将 Word 转换为 WordQueryResponse
            return wordToQueryResponse(word);
        }

        // 不存在，去数据库查询
        QueryWrapper<Word> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("spelling",wordSpelling);
        Word word = wordMapper.selectOne(queryWrapper);
        if(word != null){
            // 存在，写入 Redis 后再返回
            JSONObject jsonObject = new JSONObject(word);
            stringRedisTemplate.opsForValue().set(key, jsonObject.toString());
            return wordToQueryResponse(word);
        }
        // 不存在，返回空
        return null;
    }

    @NotNull
    private WordQueryResponse wordToQueryResponse(Word word) {
        WordQueryResponse WordQueryResponse = new WordQueryResponse();
        WordQueryResponse.setSpelling(word.getSpelling());
        WordQueryResponse.setDefinition(word.getDefinition());
        WordQueryResponse.setPhonetic(word.getPhonetic());
        WordQueryResponse.setExampleSentence(word.getExampleSentence());
        if(word.getWordType() == 3){
            WordQueryResponse.setWordType("CET4, CET6");
        } else if(word.getWordType() == 1){
            WordQueryResponse.setWordType("CET-4");
        } else if(word.getWordType() == 2){
            WordQueryResponse.setWordType("CET-6");
        }
        return WordQueryResponse;
    }

    @Override
    public SseEmitter queryWordBySpellingByAI(String wordSpelling) {

        return bigModelUtil.queryWord(wordSpelling); // 返回 SseEmitter 对象
    }

    @Override
    public SseEmitter querySentenceByAI(String sentence) {
        return bigModelUtil.querySentence(sentence);
    }

    @Override
    public SseEmitter exerciseWords(List<String> wordList) {
        return bigModelUtil.exerciseWords(wordList);
    }
}




