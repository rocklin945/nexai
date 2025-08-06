package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AppUpdateRequest
 * @Description 更新应用请求
 * @Author: rocklin
 * @Date 2025/8/6 16:15
 * @Version 1.0
 */
@Data
public class AppUpdateRequest implements Serializable {
    /**
     * appId
     */
    @NotNull(message = "appId不能为空")
    private Long id;

    /**
     * 应用名称
     */
    @NotNull(message = "应用名称不能为空")
    private String appName;
}
