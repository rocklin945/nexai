package com.rocklin.nexai.service.impl;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.enums.UserRoleEnum;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.common.utils.JwtUtils;
import com.rocklin.nexai.mapper.UserMapper;
import com.rocklin.nexai.model.entity.User;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author: rocklin
 * @Date 2025/8/3 19:10
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    @Override
    public Long register(UserRegisterRequest req) {
        //查询用户是否已存在
        User userQuery = userMapper.query(req.getUserAccount());
        Assert.isNull(userQuery, ErrorCode.OPERATION_ERROR, "用户已存在");
        //插入
        User user = new User();
        user.setUserAccount(req.getUserAccount());
        // 加密密码
        user.setUserPassword(getEncryptPassword(req.getUserPassword()));
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setUserProfile("这个人很懒，什么都没有留下。");
        Long res = userMapper.insert(user);
        if(res == 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库异常，注册失败");
        }
        return res;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest req) {
        User user = new User();
        user.setUserAccount(req.getUserAccount());
        user.setUserPassword(getEncryptPassword(req.getUserPassword()));
        User queryUser = userMapper.queryByPassword(user);
        Assert.notNull(queryUser, ErrorCode.OPERATION_ERROR, "用户不存在或密码错误");
        
        // 生成JWT token
        String token = jwtUtils.generateToken(queryUser.getId().toString(), queryUser.getUserName());
        
        // 构建响应对象
        UserLoginResponse response = buildUserResponse(queryUser);
        response.setToken(token);
        return response;
    }

    @Override
    public UserLoginResponse getCurrentUser(Long userId) {
        Assert.notNull(userId, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        
        User user = userMapper.selectById(userId);
        Assert.notNull(user, ErrorCode.OPERATION_ERROR, "用户不存在");
        
        // 构建响应对象（不返回token，因为获取当前用户时不需要重新生成token）
        UserLoginResponse response = buildUserResponse(user);
        return response;
    }

    private UserLoginResponse buildUserResponse(User user) {
        UserLoginResponse response = new UserLoginResponse();
        response.setUserId(user.getId());
        response.setUserAccount(user.getUserAccount());
        response.setUserName(user.getUserName());
        response.setUserAvatar(user.getUserAvatar());
        response.setUserProfile(user.getUserProfile());
        response.setUserRole(user.getUserRole());
        return response;
    }

    private String getEncryptPassword(String userPassword) {
        final String SALT = "rocklin";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }
}
