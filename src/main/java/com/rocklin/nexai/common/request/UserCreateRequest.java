package com.rocklin.nexai.common.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserCreateRequest
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/4 16:56
 * @Version 1.0
 */
@Data
public class UserCreateRequest implements Serializable {
    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user, admin
     */
    private String userRole;
}
