package com.rocklin.nexai.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录响应
 */
@Data
public class UserLoginResponse implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * JWT token
     */
    private String token;
}