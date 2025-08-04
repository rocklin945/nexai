package com.rocklin.nexai.service.impl;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.enums.UserRoleEnum;
import com.rocklin.nexai.common.exception.Assert;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.request.PageRequest;
import com.rocklin.nexai.common.request.UpdateUserRequest;
import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.request.UserRegisterRequest;
import com.rocklin.nexai.common.response.PageResponse;
import com.rocklin.nexai.common.utils.EncryptPasswordUtil;
import com.rocklin.nexai.common.utils.JwtUtils;
import com.rocklin.nexai.mapper.UserMapper;
import com.rocklin.nexai.model.entity.User;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.model.vo.UserLoginVO;
import com.rocklin.nexai.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.stream.Collectors;

import static com.rocklin.nexai.common.constants.Constants.USER_ID;
import static org.springframework.web.context.request.RequestAttributes.REFERENCE_REQUEST;

/**
 * @ClassName UserServiceImpl
 * @Description 用户服务实现类
 * @Author: rocklin
 * @Date 2025/8/3 19:10
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final EncryptPasswordUtil encryptPasswordUtil;

    @Override
    public Long register(UserRegisterRequest req) {
        // 查询用户是否已存在
        User userQuery = userMapper.query(req.getUserAccount());
        Assert.isNull(userQuery, ErrorCode.OPERATION_ERROR, "用户已存在");
        // 插入
        User user = new User();
        user.setUserAccount(req.getUserAccount());
        // 加密密码
        user.setUserPassword(encryptPasswordUtil.getEncryptPassword(req.getUserPassword()));
        user.setUserName("无名");
        user.setUserRole(UserRoleEnum.USER.getValue());
        user.setUserProfile("这个人很懒，什么都没有留下。");
        Long res = userMapper.insert(user);
        if (res == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库异常，注册失败");
        }
        return res;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest req) {
        User user = new User();
        user.setUserAccount(req.getUserAccount());
        user.setUserPassword(encryptPasswordUtil.getEncryptPassword(req.getUserPassword()));
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
        User user = userMapper.selectById(userId);
        Assert.notNull(user, ErrorCode.OPERATION_ERROR, "用户不存在");

        // 构建响应对象（不返回token，因为获取当前用户时不需要重新生成token）
        UserLoginResponse response = buildUserResponse(user);
        return response;
    }

    @Override
    public UserLoginResponse getCurrentUser() {
        // 从当前请求中获取用户ID
        String userId = getUserIdFromRequest();
        Assert.notNull(userId, ErrorCode.UNAUTHORIZED, "用户未登录");

        // 调用已有的方法获取用户信息
        return getCurrentUser(Long.valueOf(userId));
    }

    /**
     * 从当前请求中获取用户ID
     */
    private String getUserIdFromRequest() {
        // 使用RequestContextHolder获取当前请求
        HttpServletRequest request = ((HttpServletRequest) RequestContextHolder
                .getRequestAttributes().resolveReference(REFERENCE_REQUEST));
        // 从请求属性中获取用户ID
        return (String) request.getAttribute(USER_ID);
    }

    @Override
    public void logout(Long userId) {
        // 在JWT无状态架构中，登出主要是客户端清除token
        // 服务端可以记录登出日志或进行其他清理操作
        // 这里验证用户存在即可，前端收到请求后需要清除localStorage中的token
        User user = userMapper.selectById(userId);
        Assert.notNull(user, ErrorCode.OPERATION_ERROR, "用户不存在");
    }

    @Override
    public Long createUser(User user) {
        // 查询用户是否已存在
        User userQuery = userMapper.query(user.getUserAccount());
        Assert.isNull(userQuery, ErrorCode.OPERATION_ERROR, "用户已存在");
        // 插入
        Long res = userMapper.insert(user);
        if (res == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据库异常，用户创建失败");
        }
        return user.getId();
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        Assert.notNull(user, ErrorCode.OPERATION_ERROR, "用户不存在");
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateUser(UpdateUserRequest req) {
        return userMapper.updateById(req) > 0;
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

    @Override
    public PageResponse<UserLoginVO> listUserByPage(PageRequest pageRequest) {
        // 计算偏移量
        int offset = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();

        // 查询总记录数
        long total = userMapper.countTotal();

        // 查询用户列表
        List<User> userList = userMapper.selectListWithLimit(offset, pageRequest.getPageSize(),
                pageRequest.getSortField(), pageRequest.getSortOrder());

        // 转换为VO
        List<UserLoginVO> userLoginVOList = userList.stream()
                .map(this::convertToUserLoginVO)
                .collect(Collectors.toList());

        return new PageResponse<>(userLoginVOList, total, pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    private UserLoginVO convertToUserLoginVO(User user) {
        UserLoginVO vo = new UserLoginVO();
        vo.setUserId(user.getId());
        vo.setUserAccount(user.getUserAccount());
        vo.setUserName(user.getUserName());
        vo.setUserAvatar(user.getUserAvatar());
        vo.setUserProfile(user.getUserProfile());
        vo.setUserRole(user.getUserRole());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }
}
