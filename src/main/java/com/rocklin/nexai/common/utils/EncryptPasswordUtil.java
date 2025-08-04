package com.rocklin.nexai.common.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName EncryptPasswordUtil
 * @Description 密码加密工具类
 * @Author: rocklin
 * @Date 2025/8/4 17:03
 * @Version 1.0
 */
@Component
public class EncryptPasswordUtil {
    @Value("${salt}")
    private String SALT;
    public String getEncryptPassword(String password) {
        return DigestUtils.md5DigestAsHex((password + SALT).getBytes(StandardCharsets.UTF_8));
    }
}
