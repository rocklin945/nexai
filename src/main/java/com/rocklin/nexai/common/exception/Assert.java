package com.rocklin.nexai.common.exception;

import com.rocklin.nexai.common.enums.ErrorCode;

/**
 * @ClassName Assert
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/2 11:24
 * @Version 1.0
 */
public class Assert {
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new BusinessException(-1, message);
        }
    }
    public static void isTrue(boolean expression, ErrorCode errorCode) {
        if (!expression) {
            throw new BusinessException(errorCode);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode, String message) {
        if (!expression) {
            throw new BusinessException(errorCode, message);
        }
    }
}
