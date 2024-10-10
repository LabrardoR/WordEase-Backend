package com.head.wordeasebackend.utils;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.head.wordeasebackend.model.entity.Word;
import com.head.wordeasebackend.model.response.WordSearchResponse;
import com.head.wordeasebackend.service.WordService;
import com.zhipu.oapi.ClientV4;

import java.util.ArrayList;
import java.util.List;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;

public class BigModelUtil {


    private static final String API_KEY = "f0dc509c068e7af7f3b8dbb32857db7c.FecUTstunAmDalgE";
    private static final ClientV4 client = new ClientV4.Builder(API_KEY)
            .build();

    // 请自定义自己的业务id
    private static final String requestIdTemplate = "head-%d";

    public static String searchWord(String wordSpelling){
        String question = "请帮我查询单词' "+ wordSpelling +" '的中文意思，给出详细的解释" +
                " 用json格式返回，格式如下：\n" +
                "  {\"spelling\":\"单词拼写\",\"phonetic\":\"单词音标\",\"definition\":\"单词释义(要用中文给出)\",\"exampleSentence\":\"单词例句\",\"wordType\":\"单词类型\"}  \n" +
                "                + \"注意，第一，“单词类型”部分要根据实际情况返回CET-4或CET-6或雅思或托福等等，\" +\n" +
                "                \"第二，除了JSON字符串以外不要返回任何其他的内容\"\n" +
                "                + \"如果单词不存在，返回null\"";
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), question);
        messages.add(chatMessage);
        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);


        String jsonString = (String) invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent();
        //WordSearchResponse wordSearchResponse = JSONUtil.toBean(jsonString, WordSearchResponse.class);
        return jsonString;
    }
    public static void main(String[] args) {

//        WordSearchResponse wordSearchResponse = searchWord("abuse");
//        System.out.println("这是" + wordSearchResponse);
    }
}