package com.rocklin.nexai.common.interceptor;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JwtInterceptor单元测试
 */
@ExtendWith(MockitoExtension.class)
class JwtInterceptorTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    @InjectMocks
    private JwtInterceptor jwtInterceptor;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testPreHandleWithValidToken() throws Exception {
        // 设置mock行为
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtUtils.validateToken("valid.jwt.token")).thenReturn(true);
        when(jwtUtils.getUserIdFromToken("valid.jwt.token")).thenReturn("12345");

        // 执行测试
        boolean result = jwtInterceptor.preHandle(request, response, handlerMethod);

        // 验证结果
        assertTrue(result);
        verify(request).setAttribute("userId", "12345");
    }

    @Test
    void testPreHandleWithoutAuthorizationHeader() {
        // 设置mock行为
        when(request.getHeader("Authorization")).thenReturn(null);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jwtInterceptor.preHandle(request, response, handlerMethod);
        });

        assertEquals(ErrorCode.UNAUTHORIZED.getStatusCode(), exception.getStatusCode());
        assertEquals("未提供token", exception.getMessage());
    }

    @Test
    void testPreHandleWithInvalidTokenFormat() {
        // 设置mock行为
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jwtInterceptor.preHandle(request, response, handlerMethod);
        });

        assertEquals(ErrorCode.UNAUTHORIZED.getStatusCode(), exception.getStatusCode());
        assertEquals("未提供token", exception.getMessage());
    }

    @Test
    void testPreHandleWithInvalidToken() throws Exception {
        // 设置mock行为
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
        when(jwtUtils.validateToken("invalid.jwt.token")).thenReturn(false);

        // 执行测试并验证异常
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            jwtInterceptor.preHandle(request, response, handlerMethod);
        });

        assertEquals(ErrorCode.UNAUTHORIZED.getStatusCode(), exception.getStatusCode());
        assertEquals("token无效", exception.getMessage());
    }

    @Test
    void testPreHandleWithNonHandlerMethod() throws Exception {
        // 测试非HandlerMethod类型的处理器
        Object nonHandlerMethod = new Object();

        // 执行测试
        boolean result = jwtInterceptor.preHandle(request, response, nonHandlerMethod);

        // 验证结果
        assertTrue(result);
        verifyNoInteractions(jwtUtils);
    }
}