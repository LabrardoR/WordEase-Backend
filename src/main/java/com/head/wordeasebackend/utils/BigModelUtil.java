package com.head.wordeasebackend.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BigModelUtil {

    @Resource
    private ObjectMapper objectMapper;

    public SseEmitter init(String jsonBody) {
        SseEmitter emitter = createSeeEmitter();
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
                        if (line.isEmpty()) {
                            continue;
                        }
                        if (line.equals("[DONE]")) {
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

    private SseEmitter createSeeEmitter() {
        return new SseEmitter();
    }
    private SseEmitter callBigModel(String str){
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "glm-4");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", str);
        messages.add(message);
        requestBody.put("messages", messages);
        requestBody.put("stream", true);

        String jsonBody = null;
        try {
            jsonBody = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return init(jsonBody);
    }

    public SseEmitter queryWord(String wordSpelling) {
        String str =  "为我查询" + wordSpelling + "的意思，返回的信息参照如下模板：“此单词翻译来自AI：释义：xxxxx，音标xxx，例句xxxxxx，单词类型xx”，注意：1.例句中每个单词之间的空格要替换成“~”，如'I~am~a~student.'；2.单词的属性要在释义中给出，如n/v/adj；3.单词类型是指的cet-4、cet-6、雅思、托福等等";
        return callBigModel(str);
    }

    public SseEmitter querySentence(String sentence) {
        String str = "为我查询" + sentence + "的释义，返回的信息参照如下模板：“此释义来自AI：xxxx";
        return callBigModel(str);
    }

    public SseEmitter exerciseWords(List<String> wordList) {
        String str = "根据下列单词生成一篇不超过100词的英语文章，并在给出文章的翻译，便于我学习英语单词，单词列表如下：" + wordList + "，返回的信息参照如下模板：“This is English article：xxxxxx"
                   + "这是中文翻译：xxxx";
        return callBigModel(str);
    }
}
