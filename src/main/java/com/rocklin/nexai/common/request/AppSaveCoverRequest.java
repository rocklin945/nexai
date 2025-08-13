package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName AppsaveCoverRequest
 * @Description 保存封面请求
 * @Author: rocklin
 * @Date 2025/8/13 20:26
 * @Version 1.0
 */
@Data
public class AppSaveCoverRequest implements Serializable {

    @NotNull(message = "appId不能为空")
    private Long id;

    @NotBlank(message = "url不能为空")
    private String url;
}
