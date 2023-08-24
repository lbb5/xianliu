package com.example.xianliu.aop;

import java.lang.annotation.*;

@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RedisRateLimiter {

    double limit() default Double.MAX_VALUE;
}
