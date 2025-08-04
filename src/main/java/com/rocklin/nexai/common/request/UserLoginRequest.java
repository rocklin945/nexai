package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserLoginRequest
 * @Description 用户登录请求
 * @Author: rocklin
 * @Date 2025/8/4 8:19
 * @Version 1.0
 */
@Data
public class UserLoginRequest implements Serializable {
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(min = 3, max = 12, message = "账号长度不能小于3位，不能大于12位")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 16, message = "密码长度不能小于8位，不能大于16位")
    private String userPassword;
}
