package com.head.wordeasebackend.exception;

import com.head.wordeasebackend.common.BaseResponse;
import com.head.wordeasebackend.common.ErrorCode;
import com.head.wordeasebackend.common.Result;
import com.head.wordeasebackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(BusinessException.class)
//    public Result businessExceptionHandler(BusinessException e) {
//        log.error("BusinessException: " + e.getMessage());
//        return Result.fail();
//    }

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return Result.fail("运行时出现异常");
    }
}
