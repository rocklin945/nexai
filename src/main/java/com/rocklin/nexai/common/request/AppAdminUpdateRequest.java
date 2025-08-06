package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AppAdminUpdateRequest
 * @Description app管理员更新请求
 * @Author: rocklin
 * @Date 2025/8/6 20:01
 * @Version 1.0
 */
@Data
public class AppAdminUpdateRequest implements Serializable {
    /**
     * appId
     */
    @NotNull(message = "appId不能为空")
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 优先级
     */
    private Integer priority;
}
