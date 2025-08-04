package com.rocklin.nexai.service;

import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.model.vo.UserLoginResponse;

public interface UserService {
    Long register(UserRegisterRequest req);

    UserLoginResponse login(UserLoginRequest req);

    UserLoginResponse getCurrentUser(Long userId);
}
