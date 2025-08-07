package com.rocklin.nexai.service;

import com.rocklin.nexai.common.request.UpdateUserRequest;
import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserPageQueryRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.model.entity.User;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.model.vo.UserLoginVO;

import java.util.List;

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

    User getUserById(Long id);

    void deleteUser(Long id);

    void updateUser(UpdateUserRequest req);

    /**
     * 分页获取用户列表（带过滤条件）
     */
    PageResponse<UserLoginVO> listUserByPageWithFilter(UserPageQueryRequest request);

    List<User> batchGetUserById(List<Long> ids);
}
