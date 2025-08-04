package com.rocklin.nexai.common.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName UserPageQueryRequest
 * @Description 用户分页查询请求
 * @Author: rocklin
 * @Date 2025/8/4 21:03
 * @Version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserPageQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;
}
