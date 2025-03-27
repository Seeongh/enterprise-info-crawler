package com.antock.enterprise_info_crawler.common.exception;

import com.antock.enterprise_info_crawler.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //Enum 매핑 실패시 Exception
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<?> handleInvalidEnum(Exception e) {
        return ApiResponse.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}