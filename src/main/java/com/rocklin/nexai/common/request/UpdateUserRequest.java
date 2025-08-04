package com.rocklin.nexai.common.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UpdateUserRequest
 * @Description 更新用户请求
 * @Author: rocklin
 * @Date 2025/8/4 19:28
 * @Version 1.0
 */
@Data
public class UpdateUserRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;
}
