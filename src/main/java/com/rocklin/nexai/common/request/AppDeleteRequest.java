package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AppDeleteRequest
 * @Description 删除应用请求
 * @Author: rocklin
 * @Date 2025/8/6 16:36
 * @Version 1.0
 */
@Data
public class AppDeleteRequest implements Serializable {
    /**
     * appId
     */
    @NotNull(message = "appId不能为空")
    private Long id;
}
