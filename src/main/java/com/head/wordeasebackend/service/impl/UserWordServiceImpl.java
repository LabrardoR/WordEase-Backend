package com.head.wordeasebackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.head.wordeasebackend.model.entity.UserWord;
import com.head.wordeasebackend.service.UserWordService;
import com.head.wordeasebackend.mapper.UserWordMapper;
import org.springframework.stereotype.Service;

/**
* @author headhead
* @description 针对表【user_word(用户单词关联表)】的数据库操作Service实现
* @createDate 2024-09-14 21:53:38
*/
@Service
public class UserWordServiceImpl extends ServiceImpl<UserWordMapper, UserWord>
    implements UserWordService{

}




