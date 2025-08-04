package com.rocklin.nexai.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserLoginVO
 * @Description 当前登录用户信息
 * @Author: rocklin
 * @Date 2025/8/4 15:02
 * @Version 1.0
 */
@Data
public class UserLoginVO implements Serializable {
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
    private String userRole;
}
