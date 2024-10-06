package com.head.wordeasebackend.controller;


import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.dto.WordToListRequest;
import com.head.wordeasebackend.model.dto.WordDto;
import com.head.wordeasebackend.model.dto.WordRequestDTO;
import com.head.wordeasebackend.model.entity.SafetyUser;
import com.head.wordeasebackend.model.entity.User;
import com.head.wordeasebackend.model.request.TokenRequest;
import com.head.wordeasebackend.service.UserService;
import com.head.wordeasebackend.service.UserWordService;
import com.head.wordeasebackend.service.WordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
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

    @PostMapping("/search")
    public Result queryWord(@RequestBody WordRequestDTO wordRequestDTO){

        String wordSpelling = wordRequestDTO.getWordSpelling();
        if(wordSpelling == null){
            return Result.fail("单词拼写不能为空");
        }
        System.out.println(wordSpelling);
        WordDto wordDto = wordService.queryWordBySpelling(wordSpelling);
        if(wordDto == null){
            return Result.fail("单词不存在");
        }
        return Result.ok(wordDto);
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
        return Result.ok(result);
    }


    /**
     * 查询用户的单词表
     * @param tokenRequest token封装类
     * @return 单词列表封装类
     */
    @GetMapping("/getWordList")
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
     * @param wordDtoList
     * @return
     */
    @PostMapping("/test")
    public Result addWordsToList(@RequestBody List<WordDto> wordDtoList){

        return Result.ok();
    }

}
