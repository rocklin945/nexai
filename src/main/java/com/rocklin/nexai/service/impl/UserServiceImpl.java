package com.rocklin.nexai.service.impl;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.enums.UserRoleEnum;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.mapper.UserMapper;
import com.rocklin.nexai.model.entity.User;
import com.rocklin.nexai.model.vo.LoginUserVO;
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
    public LoginUserVO login(UserLoginRequest req) {
        User user = new User();
        user.setUserAccount(req.getUserAccount());
        user.setUserPassword(getEncryptPassword(req.getUserPassword()));
        User queryUser = userMapper.queryByPassword(user);
        Assert.notNull(queryUser, ErrorCode.OPERATION_ERROR, "用户不存在");
    }

    private String getEncryptPassword(String userPassword) {
        final String SALT = "rocklin";
        return DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes(StandardCharsets.UTF_8));
    }
}
