package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName DeleteRequest
 * @Description 删除请求
 * @Author: rocklin
 * @Date 2025/8/6 19:55
 * @Version 1.0
 */
@Data
public class DeleteRequest implements Serializable {
    /**
     * appId
     */
    @NotNull(message = "appId不能为空")
    private Long id;
}
