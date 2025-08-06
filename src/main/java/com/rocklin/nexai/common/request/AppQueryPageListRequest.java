package com.rocklin.nexai.common.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName AppCurUserListRequest
 * @Description 获取当前用户应用列表请求
 * @Author: rocklin
 * @Date 2025/8/6 16:58
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryPageListRequest extends PageRequest implements Serializable {
    /**
     * appId
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    /**
     * 代码生成类型（枚举）
     */
    private String codeGenType;

    /**
     * 部署标识
     */
    private String deployKey;

    /**
     * 优先级 默认为0 精选为1
     */
    private Integer priority;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 编辑时间
     */
    private LocalDateTime editTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
