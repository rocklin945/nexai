package com.rocklin.nexai.service;

import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.model.entity.User;
import com.rocklin.nexai.model.vo.UserLoginResponse;

public interface UserService {
    Long register(UserRegisterRequest req);

    UserLoginResponse login(UserLoginRequest req);

    /**
     * 根据用户ID获取当前用户
     */
    UserLoginResponse getCurrentUser(Long userId);

    /**
     * 获取当前登录用户（从JWT中获取用户ID）
     */
    UserLoginResponse getCurrentUser();

    void logout(Long userId);

    Long createUser(User user);
}
