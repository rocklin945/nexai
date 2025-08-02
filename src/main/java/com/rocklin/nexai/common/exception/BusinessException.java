package com.rocklin.nexai.common.exception;

import com.rocklin.nexai.common.enums.ErrorCode;
import lombok.Getter;

/**
 * @ClassName BusinessException
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/2 11:22
 * @Version 1.0
 */
@Getter
public class BusinessException extends RuntimeException{
    private final int statusCode;

    public BusinessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public BusinessException(ErrorCode errorCode) {
        this(errorCode.getStatusCode(), errorCode.getMessage());
    }

    public BusinessException(ErrorCode errorCode, String message) {
        this(errorCode.getStatusCode(), message);
    }
}
