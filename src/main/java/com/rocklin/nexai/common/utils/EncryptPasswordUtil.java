package com.rocklin.nexai.common.utils;

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName EncryptPasswordUtil
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/4 17:03
 * @Version 1.0
 */
public class EncryptPasswordUtil {
    public static String getEncryptPassword(String password) {
        final String SALT = "rocklin";
        return DigestUtils.md5DigestAsHex((password + SALT).getBytes(StandardCharsets.UTF_8));
    }
}
