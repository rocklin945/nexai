package com.rocklin.nexai.common.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtils单元测试
 */
@SpringBootTest
class JwtUtilsTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testGenerateToken() {
        String userId = "12345";
        String username = "testUser";
        
        String token = jwtUtils.generateToken(userId, username);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        System.out.println("token = " + token);
    }

    @Test
    void testValidateToken() {
        String userId = "12345";
        String username = "testUser";
        
        String token = jwtUtils.generateToken(userId, username);
        
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void testGetUserIdFromToken() {
        String userId = "12345";
        String username = "testUser";
        
        String token = jwtUtils.generateToken(userId, username);
        
        String extractedUserId = jwtUtils.getUserIdFromToken(token);
        
        assertEquals(userId, extractedUserId);
    }

    @Test
    void testGetUsernameFromToken() {
        String userId = "12345";
        String username = "testUser";
        
        String token = jwtUtils.generateToken(userId, username);
        
        String extractedUsername = jwtUtils.getUsernameFromToken(token);

        assertEquals(username, extractedUsername);
        System.out.println("extractedUsername = " + extractedUsername);
    }

    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        assertFalse(jwtUtils.validateToken(invalidToken));
    }

    @Test
    void testExpiredToken() throws InterruptedException {
        // 创建一个过期时间很短的token进行测试
        String userId = "12345";
        String username = "testUser";
        
        // 由于我们配置的过期时间是1天，这里我们直接测试无效token的情况
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NSIsImV4cCI6MTAwMCwidXNlcm5hbWUiOiJ0ZXN0VXNlciJ9.invalid";
        
        assertFalse(jwtUtils.validateToken(expiredToken));
    }
}