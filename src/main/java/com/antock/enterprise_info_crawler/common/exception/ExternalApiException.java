package com.antock.enterprise_info_crawler.common.exception;

import org.springframework.http.HttpStatus;

public class ExternalApiException extends CustomException{
    public ExternalApiException(HttpStatus httpStatus, String resultMsg) {
        super(httpStatus, resultMsg);
    }
}
