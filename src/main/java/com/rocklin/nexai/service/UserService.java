package com.rocklin.nexai.service;

import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.model.vo.LoginUserVO;

public interface UserService {
    Long register(UserRegisterRequest req);

    LoginUserVO login(UserLoginRequest req);
}
