package com.rocklin.nexai.common.exception;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName GlobalExceptionHandler
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/2 11:28
 * @Version 1.0
 */
@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        return BaseResponse.error(e.getStatusCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException: {}", e.getMessage());
        return BaseResponse.error(ErrorCode.OPERATION_ERROR.getStatusCode(),e.getMessage());
    }

    // 参数校验异常 - @Valid / @Validated 参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验异常: {}", msg);
        return BaseResponse.error(ErrorCode.PARAMS_ERROR.getStatusCode(), msg);
    }

    // 参数校验异常 - @RequestParam / @PathVariable 校验失败
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("参数校验异常: {}", msg);
        return BaseResponse.error(ErrorCode.PARAMS_ERROR.getStatusCode(), msg);
    }

    // 捕获所有未处理的异常，防止程序崩溃
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleException(Exception e) {
        log.error("未知异常: ", e);
        return BaseResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getStatusCode(), "未知错误，请联系管理员");
    }
}
