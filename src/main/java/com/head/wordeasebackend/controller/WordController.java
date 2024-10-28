package com.head.wordeasebackend.controller;



import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.request.*;
import com.head.wordeasebackend.model.response.WordQueryResponse;
import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.service.UserService;
import com.head.wordeasebackend.service.UserWordService;
import com.head.wordeasebackend.service.WordService;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.lang.annotation.Documented;
import java.util.List;

@RestController
@Slf4j
@RequestMapping ("/word")
public class WordController {
    @Resource
    private WordService wordService;
    @Resource
    private UserService userService;
    @Resource
    private UserWordService userWordService;

    @Resource
    private RestTemplate restTemplate;


    /**
     * 搜索单词
     * @param wordSpelling 单词拼写
     * @return 单词搜索响应类
     */
    @GetMapping("/queryWord")
    public SseEmitter queryWord(@RequestParam("wordSpelling") String wordSpelling){
        if(wordSpelling == null || wordSpelling.trim().isEmpty()){
            SseEmitter emitter = new SseEmitter();
            try{
                emitter.send("单词拼写不能为空".getBytes());
                emitter.complete();
            }catch (Exception e){
                e.printStackTrace();
            }
            return emitter;
        }
        WordQueryResponse wordQueryResponse = wordService.queryWordBySpelling(wordSpelling);
        if(wordQueryResponse != null){
            SseEmitter emitter = new SseEmitter();
            try {
                // 将现有结果发送到客户端
                emitter.send(Result.ok(wordQueryResponse));
                emitter.complete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return emitter;
        }else {
            // 调用 AI 查询
            return wordService.queryWordBySpellingByAI(wordSpelling);
        }
    }

    /**
     * 查询句子翻译
     * @param sentence 例句
     * @return 流式输出结果
     */
    @GetMapping("/querySentence")
    public SseEmitter querySentence(@RequestParam("sentence") String sentence){
        if(sentence == null || sentence.trim().isEmpty()){
            SseEmitter emitter = new SseEmitter();
            try{
                emitter.send("句子不能为空".getBytes());
                emitter.complete();
            }catch (Exception e){
                e.printStackTrace();
            }
            return emitter;
        }
        return wordService.querySentenceByAI(sentence);
    }

    @GetMapping("/exerciseWords")
    public SseEmitter exerciseWords(@RequestParam("wordList") List<String> wordList){
        if(wordList == null || wordList.isEmpty()){
            SseEmitter emitter = new SseEmitter();
            try{
                emitter.send("单词不能为空".getBytes());
                emitter.complete();
            }catch (Exception e){
                e.printStackTrace();
            }
            return emitter;
        }

        // todo 生成一个对话，方便背单词，并返回，

        return wordService.exerciseWords(wordList);
    }
    @GetMapping("/checkAnswer")
    public Result checkAnswer(@RequestParam("answerList") List<String> answerList){

        return wordService.checkAnswer(answerList);
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
     * @param WordQueryResponseList
     * @return
     */

    @PostMapping("/test")
    public Result addWordsToList(@RequestBody List<WordQueryResponse> WordQueryResponseList){

        return Result.ok();
    }

}
