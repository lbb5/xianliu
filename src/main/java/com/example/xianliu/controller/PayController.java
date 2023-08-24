package com.example.xianliu.controller;

import com.example.xianliu.aop.MyRateLimiter;
import com.example.xianliu.aop.RedisRateLimiter;
import com.example.xianliu.service.PayService;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author libingbing
 */
@RestController
@RequestMapping("/xiianliu")
public class PayController {

    @Autowired
    public PayService payService;
    //每秒只能请求一次
    private RateLimiter rateLimiter = RateLimiter.create(1);

    @GetMapping("/pay")
    public String pay(){
        //尝试500毫秒内获取到令牌则允许正常运行，否则降级处理
        boolean flag = rateLimiter.tryAcquire(500, TimeUnit.MILLISECONDS);
        if(flag){
            return payService.pay(new BigDecimal(100));
        }
        return "失败了，再试一次吧";
    }

    @GetMapping("/pay2")
    @MyRateLimiter(value = 1,time = 500)
    public String pay2(){
       return payService.pay(new BigDecimal(100));
    }

    @GetMapping("/pay3")
    @RedisRateLimiter(limit = 1)
    public String pay3(){
        return payService.pay(new BigDecimal(100));
    }

}
