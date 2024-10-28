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
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BigModelUtil {

    @Resource
    private ObjectMapper objectMapper;


    private final List<String> correctAnswers = new ArrayList<>(); // 用于存储正确答案
    private final StringBuffer stringBuffer = new StringBuffer();

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
                            // 收到 [DONE] 信号，停止读取
                            // 此处对 stringBuffer 中的数据进行处理，将每个答案都加入到 correctAnswers 列表中
                            // todo

                            //System.out.println(stringBuffer.toString());
                            String str = stringBuffer.toString();
                            Pattern pattern = Pattern.compile("\\d+:([A-D])");
                            Matcher matcher = pattern.matcher(str);
                            while(matcher.find()){
                                correctAnswers.add(matcher.group(1));
                            }
                            System.out.println(correctAnswers);
                            break;
                        }
                        JSONObject jsonObject = new JSONObject(line);
                        JSONArray choices = jsonObject.getJSONArray("choices");
                        String content = choices.getJSONObject(0).getJSONObject("delta").getStr("content");
                        if (content != null) {
                            // 仅将非答案内容发送给前端
                            stringBuffer.append(content);
                            System.out.print(content);
                            emitter.send(content);
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


    public List<String> getCorrectAnswers() {
        return new ArrayList<>(correctAnswers); // 返回正确答案的副本
    }

    public SseEmitter queryWord(String wordSpelling) {
        String str =  "为我查询" + wordSpelling + "的意思，返回的信息参照如下模板：“此单词翻译来自AI：释义：xxxxx，音标xxx，例句xxxxxx，单词类型xx”，注意：1.例句中每个单词之间的空格要替换成“~”，如'I~am~a~student.'；2.单词的属性要在释义中给出，如n/v/adj；3.单词类型是指的cet-4、cet-6、雅思、托福等等";
        return callBigModel(str);
    }

    public SseEmitter querySentence(String sentence) {
        String str = "为我查询" + sentence + "的释义，返回的信息参照如下模板：“此释义来自AI：xxxx";
        return callBigModel(str);
    }


    public SseEmitter exerciseWords(List<String> wordList, List<String> answerList) {
        String str = "根据下列单词生成一篇不超过100词的英语文章，并在给出文章的翻译，便于我学习记忆这几个英语单词，单词列表如下：" + wordList
                + "接下来，你还要要为这些单词生成三道练习题，并为每道题生成A、B、C、D四个选项，在全文的最后给出所有题目的答案，答案的格式示例为Answers:{1:A,2:B,3:D}" ;
        answerList = getCorrectAnswers();
        return callBigModel(str);
    }
    public SseEmitter getAnswer() {

        String str = "请给出刚才三道题的答案，答案示例{{1:A,2:B,3:C}}";
        System.out.println("答案为: ");
        return callBigModel(str);
    }

}
