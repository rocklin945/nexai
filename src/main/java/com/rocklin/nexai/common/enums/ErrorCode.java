package com.rocklin.nexai.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PARAMS_ERROR(666400, "参数错误"),
    NOT_FOUND(666404, "资源未找到"),
    UNAUTHORIZED(666401, "未认证"),
    TOO_MANY_REQUESTS(666429, "请求过于频繁"),
    INTERNAL_SERVER_ERROR(666500, "系统内部异常"),
    OPERATION_ERROR(666501, "操作失败"),
    ;
    private final int statusCode;
    private final String message;

    ErrorCode(int code, String message) {
        this.statusCode = code;
        this.message = message;
    }
}
