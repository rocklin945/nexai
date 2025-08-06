package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AppGetByIdRequest
 * @Description 获取应用详情请求
 * @Author: rocklin
 * @Date 2025/8/6 16:49
 * @Version 1.0
 */
@Data
public class AppGetByIdRequest implements Serializable {
    /**
     * 应用id
     */
    @NotNull(message = "appId不能为空")
    private Long id;
}
