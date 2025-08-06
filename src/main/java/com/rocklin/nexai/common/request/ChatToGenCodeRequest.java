package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ChatToGenCodeRequest
 * @Description 聊天生成代码请求
 * @Author: rocklin
 * @Date 2025/8/5 21:22
 * @Version 1.0
 */
@Data
public class ChatToGenCodeRequest implements Serializable {
    /**
     * 应用id
     */
    @NotNull(message = "应用id不能为空")
    Long appId;

    /**
     * 聊天内容
     */
    @NotBlank(message = "聊天内容不能为空")
    String message;
}
