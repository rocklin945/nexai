package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserRegisterRequest
 * @Description 用户注册请求
 * @Author: rocklin
 * @Date 2025/8/3 19:24
 * @Version 1.0
 */
@Data
public class UserRegisterRequest implements Serializable {
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 16, message = "密码长度不能小于8位，不能大于16位")
    private String userPassword;

    /**
     * 确认密码
     */
    @NotBlank
    @Size(min = 8, max = 16, message = "密码长度不能小于8位，不能大于16位")
    private String checkPassword;
}
