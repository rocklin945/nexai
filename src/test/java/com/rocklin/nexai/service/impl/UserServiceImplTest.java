package com.rocklin.nexai.service.impl;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.request.UserLoginRequest;
import com.rocklin.nexai.common.utils.JwtUtils;
import com.rocklin.nexai.mapper.UserMapper;
import com.rocklin.nexai.model.entity.User;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private UserLoginRequest loginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        loginRequest = new UserLoginRequest();
        loginRequest.setUserAccount("testUser");
        loginRequest.setUserPassword("password123");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUserAccount("testUser");
        mockUser.setUserName("Test User");
        mockUser.setUserAvatar("avatar.jpg");
        mockUser.setUserRole("user");
    }

    @Test
    void testLoginSuccess() {
        // 设置mock行为
        when(userMapper.queryByPassword(any(User.class))).thenReturn(mockUser);
        when(jwtUtils.generateToken("1", "Test User")).thenReturn("mock.jwt.token");

        // 执行测试
        UserLoginResponse response = userService.login(loginRequest);

        // 验证结果
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("testUser", response.getUserAccount());
        assertEquals("Test User", response.getUserName());
        assertEquals("avatar.jpg", response.getUserAvatar());
        assertEquals("user", response.getUserRole());
        assertEquals("mock.jwt.token", response.getToken());

        // 验证mock调用
        verify(userMapper, times(1)).queryByPassword(any(User.class));
        verify(jwtUtils, times(1)).generateToken("1", "Test User");
    }

    @Test
    void testLoginUserNotFound() {
        // 设置mock行为
        when(userMapper.queryByPassword(any(User.class))).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(loginRequest);
        });

        assertEquals(ErrorCode.OPERATION_ERROR.getStatusCode(), exception.getStatusCode());
        assertEquals("用户不存在或密码错误", exception.getMessage());

        // 验证mock调用
        verify(userMapper, times(1)).queryByPassword(any(User.class));
        verify(jwtUtils, never()).generateToken(any(), any());
    }

    @Test
    void testLoginWrongPassword() {
        // 设置mock行为
        when(userMapper.queryByPassword(any(User.class))).thenReturn(null);

        // 创建错误的密码请求
        UserLoginRequest wrongPasswordRequest = new UserLoginRequest();
        wrongPasswordRequest.setUserAccount("testUser");
        wrongPasswordRequest.setUserPassword("wrongPassword");

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            userService.login(wrongPasswordRequest);
        });

        assertEquals(ErrorCode.OPERATION_ERROR.getStatusCode(), exception.getStatusCode());
        assertEquals("用户不存在或密码错误", exception.getMessage());
    }

    @Test
    void testPasswordEncryption() {
        // 测试密码加密逻辑
        String encrypted = DigestUtils.md5DigestAsHex("password123".getBytes());
        assertNotNull(encrypted);
        assertEquals(32, encrypted.length()); // MD5哈希长度为32位
    }
}