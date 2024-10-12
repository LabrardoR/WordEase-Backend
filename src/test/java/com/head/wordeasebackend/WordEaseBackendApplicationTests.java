package com.head.wordeasebackend;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
class WordEaseBackendApplicationTests {
    @Autowired
    private RestTemplate restTemplate;
    @Test
    void contextLoads() {
        String wordSpelling = "abandon";
        String url = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
        String apiKey = "f0dc509c068e7af7f3b8dbb32857db7c.FecUTstunAmDalgE";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",apiKey);
        headers.set("Content-Type","application/json");
        String jsonBody = "{\n" +
                "    \"model\": \"glm-4\",\n" +
                "    \"messages\":[\n" +
                "        {\n" +
                "            \"role\": \"user\", \n" +
                "            \"content\": \"为我查询abandon的意思，返回的信息参照如下模板：“此单词翻译来自AI：释义：xxxxx，音标xxx，例句xxxxxx，单词类型xx”，注意：单词的属性要在释义中给出，如n/v/adj，单词类型是指的cet-4、cet-6等等\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"stream\": true\n" +
                "}";
        // 创建 HttpEntity，包含头信息和请求体
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);
        String response = restTemplate.postForObject(url, entity, String.class);
        System.out.println(response);
    }

}
