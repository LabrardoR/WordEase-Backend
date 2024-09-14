package com.head.wordeasebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.model.entity.Word;
import com.head.wordeasebackend.service.WordService;
import com.head.wordeasebackend.mapper.WordMapper;
import org.springframework.stereotype.Service;

/**
* @author headhead
* @description 针对表【word(常见单词词库)】的数据库操作Service实现
* @createDate 2024-09-14 21:53:38
*/
@Service
public class WordServiceImpl extends ServiceImpl<WordMapper, Word>
    implements WordService{

}




