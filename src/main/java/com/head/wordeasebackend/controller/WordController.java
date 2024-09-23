package com.head.wordeasebackend.controller;


import com.head.wordeasebackend.common.BaseResponse;
import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.model.dto.WordDto;
import com.head.wordeasebackend.model.dto.WordRequestDTO;
import com.head.wordeasebackend.service.WordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/word")
@CrossOrigin
public class WordController {
    @Resource
    private WordService wordService;

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

}
