package com.head.wordeasebackend.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.model.response.WordSearchResponse;
import com.head.wordeasebackend.model.entity.Word;
import com.head.wordeasebackend.service.WordService;
import com.head.wordeasebackend.mapper.WordMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    @Override
    public SseEmitter queryWordBySpellingByAI(String wordSpelling) {

        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                String url = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
                String apiKey = "f0dc509c068e7af7f3b8dbb32857db7c.FecUTstunAmDalgE";

                // 创建连接
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", apiKey);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // 构建请求体
                String jsonBody = "{\n" +
                        "    \"model\": \"glm-4\",\n" +
                        "    \"messages\":[\n" +
                        "        {\n" +
                        "            \"role\": \"user\", \n" +
                        "            \"content\": \"为我查询" + wordSpelling + "的意思，返回的信息参照如下模板：“此单词翻译来自AI：释义：xxxxx，音标xxx，例句xxxxxx，单词类型xx”，注意：1.例句中每个单词之间的空格要替换成“~”，如'I~am~a~student.'；2.单词的属性要在释义中给出，如n/v/adj；3.单词类型是指的cet-4、cet-6、雅思、托福等等\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"stream\": true\n" +
                        "}";

                // 发送请求体
                connection.getOutputStream().write(jsonBody.getBytes("UTF-8"));
                connection.getOutputStream().flush();

                // 读取响应
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // 去掉 'data:' 前缀
                        if (line.startsWith("data: ")) {
                            line = line.split(": ", 2)[1].trim();
                        }
                        if(line.isEmpty()){
                            continue;
                        }
                        if(line.equals("[DONE]")){
                            break;
                        }
                        JSONObject jsonObject = new JSONObject(line);
                        JSONArray choices = jsonObject.getJSONArray("choices");
                        String content = choices.getJSONObject(0).getJSONObject("delta").getStr("content");
                        if (content != null) {
                            System.out.print(content);
                            emitter.send(content); // 逐行发送数据到客户端
                        }
                    }
                }

                emitter.complete(); // 完成响应
            } catch (Exception e) {
                try {
                    emitter.send("出现了一些错误.".getBytes());
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();

        return emitter; // 返回 SseEmitter 对象
    }
}




