package com.rocklin.nexai.common.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ChatHistoryDeleteRequest
 * @Description 删除对话历史请求
 * @Author: rocklin
 * @Date 2025/8/18 15:26
 * @Version 1.0
 */
@Data
public class ChatHistoryDeleteRequest implements Serializable {
    @NotNull(message = "id不能为空")
    private Long id;
}
