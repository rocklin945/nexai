package com.rocklin.nexai.service;

import com.rocklin.nexai.common.request.UserRegisterRequest;

public interface UserService {
    Long register(UserRegisterRequest req);
}
