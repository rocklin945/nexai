package com.rocklin.nexai.common.interceptor;

import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 * 用于验证用户请求的token
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未提供token");
        }

        // 提取token,去掉Bearer
        token = token.substring(7);
        
        try {
            // 验证token
            if (!jwtUtils.validateToken(token)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED, "token无效");
            }
            
            // 从token中获取用户ID并设置到请求属性中
            String userId = jwtUtils.getUserIdFromToken(token);
            request.setAttribute("userId", userId);
            
            return true;
        }catch (BusinessException e){
            throw e;
        }catch (Exception e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "token验证失败");
        }
    }
}