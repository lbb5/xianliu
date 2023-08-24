package com.example.xianliu.aop;

import java.lang.annotation.*;

/**
 * @author libingbing
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface MyRateLimiter {

     int value();

     int time();

}
