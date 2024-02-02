package com.example.controller.exception;

import com.example.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j//日志注解, 保存的对象名为log
@RestControllerAdvice
public class ValidationController {

    @ExceptionHandler(ValidationException.class)//处理Validation中的异常
    public RestBean<Void> validateException(ValidationException exception){
        log.warn("Resolve [{} : {}]", exception.getClass().getName(), exception.getMessage());//将后面的参数按顺序填入第一个参数中
        return RestBean.failure(400,"请求参数有误");
    }
}
