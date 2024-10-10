package com.head.wordeasebackend.controller;


import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.request.WordDeletedFromListRequest;
import com.head.wordeasebackend.model.request.WordToListRequest;
import com.head.wordeasebackend.model.response.WordSearchForAIResponse;
import com.head.wordeasebackend.model.response.WordSearchResponse;
import com.head.wordeasebackend.model.request.WordSearchRequest;
import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.model.request.TokenRequest;
import com.head.wordeasebackend.service.UserService;
import com.head.wordeasebackend.service.UserWordService;
import com.head.wordeasebackend.service.WordService;
import com.head.wordeasebackend.utils.BigModelUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping ("/word")
public class WordController {
    @Resource
    private WordService wordService;
    @Resource
    private UserService userService;
    @Resource
    private UserWordService userWordService;

//    @PostMapping("/search")
//    public Result queryWord(@RequestBody WordSearchRequest wordSearchRequest){
//
//        String wordSpelling = wordSearchRequest.getWordSpelling();
//        if(wordSpelling == null){
//            return Result.fail("单词拼写不能为空");
//        }
//        System.out.println(wordSpelling);
//        WordSearchResponse wordSearchResponse = wordService.queryWordBySpelling(wordSpelling);
//        if(wordSearchResponse == null){
//            // todo 调用 AI 查询
//            wordSearchResponse = BigModelUtil.searchWord(wordSpelling);
//            if(wordSearchResponse == null){
//                return Result.fail("单词不存在");
//            }
//            WordSearchForAIResponse wordSearchForAIResponse = new WordSearchForAIResponse();
//            wordSearchForAIResponse.setWordSearchResponse(wordSearchResponse);
//            wordSearchForAIResponse.setMessage("此单词在本站数据库中不存在，通过AI查询到如下释义");
//            return Result.ok(wordSearchForAIResponse);
//        }
//        return Result.ok(wordSearchResponse);
//    }

    @GetMapping("/search")
    public SseEmitter queryWord(@RequestParam String wordSpelling) {
        SseEmitter sseEmitter = new SseEmitter();
        new Thread(()->{
            try {
                sseEmitter.send("正在查询单词: " + wordSpelling);

                // 调用 AI 查询
                String response = BigModelUtil.searchWord(wordSpelling);

                // 发送结果
                int i = 5;
                while (i > 0){
                    i--;
                    sseEmitter.send(response);
                }


                sseEmitter.complete();
            } catch (IOException e) {
                sseEmitter.completeWithError(e);
            }
        }).start();
        return sseEmitter;
    }

    /**
     * 添加一个单词到用户的单词表
     * @param wordToListRequest 添加单词到单词表请求类
     * @return
     */
    @PostMapping("/addWordToList")
    public Result addWordToList(@RequestBody WordToListRequest wordToListRequest){
        String token = wordToListRequest.getToken();
        SafetyUser safetyUser = userService.getLoginUser(token);
        if(safetyUser == null){
            return Result.fail("用户未登录");
        }
        Long result = userWordService.addWordToUserWordList(safetyUser.toUser(), wordToListRequest);
        if(result == -1L){
            return Result.fail("单词已存在");
        }
        if(result == 0){
            return Result.fail("添加失败");
        }
        return Result.ok(result);
    }
    @PostMapping("/deleteWordFromList")
    public Result deleteWordFromList(@RequestBody WordDeletedFromListRequest wordDeletedFromListRequest){
        String token = wordDeletedFromListRequest.getToken();
        String wordSpelling = wordDeletedFromListRequest.getWordSpelling();
        if(token == null || wordSpelling == null) {
            return Result.fail("参数错误");
        }
        SafetyUser safetyUser = userService.getLoginUser(token);
        if(safetyUser == null){
            return Result.fail("用户未登录");
        }
        Long result = userWordService.deleteWordFromUserWordList(safetyUser.toUser(), wordDeletedFromListRequest);
        if(result == null){
            return Result.fail("删除失败");
        }
        return Result.ok("删除成功");
    }



    /**
     * 查询用户的单词表
     * @param tokenRequest token封装类
     * @return 单词列表封装类
     */
    @PostMapping("/getWordList")
    public Result getWordList(@RequestBody TokenRequest tokenRequest){
        if(tokenRequest == null){
            return Result.fail("参数错误");
        }
        String token = tokenRequest.getToken();
        if(token == null){
            return Result.fail("参数错误");
        }
        SafetyUser safetyUser = userService.getLoginUser(token);
        if(safetyUser == null){
            return Result.fail("用户未登录");
        }

        List<WordToListRequest> list = userWordService.getWordList(safetyUser.toUser());
        if(list == null){
            return Result.ok("单词表目前为空");
        }
        return Result.ok(list);
    }


    /**
     * 批量添加单词到用户的单词表 todo
     * @param wordSearchResponseList
     * @return
     */
    @PostMapping("/test")
    public Result addWordsToList(@RequestBody List<WordSearchResponse> wordSearchResponseList){

        return Result.ok();
    }

}
