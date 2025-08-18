package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AppCancelDeployRequest
 * @Description 取消应用部署请求
 * @Author: rocklin
 * @Date 2025/8/18 11:24
 * @Version 1.0
 */
@Data
public class AppCancelDeployRequest implements Serializable {
    /**
     * 应用 id
     */
    @NotNull(message = "应用id不能为空")
    private Long appId;
}
