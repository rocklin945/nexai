package com.rocklin.nexai.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SlidingWindowRateLimit {
    // 时间窗口，单位秒
    int windowInSeconds() default 60;
    // 最大请求次数
    int maxCount() default 10;
}
