package com.rocklin.nexai.common.aop;

import com.rocklin.nexai.common.annotation.AuthCheck;
import com.rocklin.nexai.common.enums.ErrorCode;
import com.rocklin.nexai.common.enums.UserRoleEnum;
import com.rocklin.nexai.common.exception.BusinessException;
import com.rocklin.nexai.model.vo.UserLoginResponse;
import com.rocklin.nexai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @ClassName AuthCheckInterceptor
 * @Description 鉴权拦截器
 * @Author: rocklin
 * @Date 2025/8/4 16:19
 * @Version 1.0
 */
@Aspect
@Component
@RequiredArgsConstructor
public class AuthCheckInterceptor {

    private final UserService userService;

    @Around("@annotation(checkAuth)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck checkAuth) throws Throwable {
        Integer role = checkAuth.enableRole().getValue();
        UserLoginResponse currentUser = userService.getCurrentUser();
        UserRoleEnum currentUserRole = UserRoleEnum.getEnumByValue(currentUser.getUserRole());
        if (currentUserRole == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        if (!currentUserRole.getValue().equals(role)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return joinPoint.proceed();
    }
}
