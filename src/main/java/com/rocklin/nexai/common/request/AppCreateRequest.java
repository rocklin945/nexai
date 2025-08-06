package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AppCreateRequest
 * @Description 应用创建请求
 * @Author: rocklin
 * @Date 2025/8/5 21:42
 * @Version 1.0
 */
@Data
public class AppCreateRequest implements Serializable {
    /**
     * 应用初始化的 prompt
     */
    @NotBlank(message = "初始化的prompt不能为空")
    private String initPrompt;
}
