package com.rocklin.nexai.common.aop;

import com.rocklin.nexai.common.annotation.SlidingWindowRateLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

import static com.rocklin.nexai.common.constants.Constants.*;

@Aspect
@Component
@RequiredArgsConstructor
public class SlidingWindowRateLimitAspect {

    private final StringRedisTemplate redisTemplate;
    private final HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, SlidingWindowRateLimit rateLimit) throws Throwable {
        String ip = getClientIp(request);
        String uri = request.getRequestURI();
        String key = SLIDING_RATE_LIMIT_KEY_PREFIX + ip + COLON + uri;

        long nowTs = System.currentTimeMillis();
        long windowSizeMs = rateLimit.windowInSeconds() * ONE_SECOND;

        // 1. 移除窗口外的请求记录
        redisTemplate.opsForZSet().removeRangeByScore(key, ZERO, nowTs - windowSizeMs);

        // 2. 当前窗口请求数
        Long count = redisTemplate.opsForZSet().zCard(key);
        if (count != null && count >= rateLimit.maxCount()) {
            throw new RuntimeException("请求太频繁，请稍后再试");
        }

        // 3. 添加当前请求
        redisTemplate.opsForZSet().add(key, String.valueOf(nowTs), nowTs);

        // 4. 设置过期时间，避免 Redis 内存泄漏
        redisTemplate.expire(key, rateLimit.windowInSeconds(), TimeUnit.SECONDS);

        return joinPoint.proceed();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        return (ip == null || ip.isEmpty()) ? request.getRemoteAddr() : ip.split(",")[0];
    }
}
