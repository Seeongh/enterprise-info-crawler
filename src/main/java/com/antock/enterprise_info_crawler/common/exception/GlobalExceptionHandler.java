package com.antock.enterprise_info_crawler.common.exception;

import com.antock.enterprise_info_crawler.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Enum 매핑 실패시 Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleInvalidEnum(MethodArgumentNotValidException ex) {  // @ValidEnum에서 설정한 메시지 처리

        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)  // 메시지 출력
                .collect(Collectors.joining(", "));

        log.warn("검증 실패: {}", errorMsg);

        return ApiResponse.of(HttpStatus.BAD_REQUEST,errorMsg,"");
    }
}