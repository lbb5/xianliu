package com.example.xianliu.aop;

import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author libingbing
 */
@Component
@Aspect
public class MyRateLimiterAspect {

    @Autowired
    public HttpServletResponse response;
    //有一个坑，一定是要全局唯一，不能进来一个new一个
    private  RateLimiter rateLimiter = RateLimiter.create(2);

    @Pointcut("@annotation(com.example.xianliu.aop.MyRateLimiter)")
    public void point(){

    }
    @Around("point()")
    public Object process(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{

        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        MyRateLimiter myRateLimiter = signature.getMethod().getDeclaredAnnotation(MyRateLimiter.class);
        if(myRateLimiter == null){
            return proceedingJoinPoint.proceed();
        }
        int time = myRateLimiter.time();
        int value = myRateLimiter.value();
        rateLimiter.setRate(value);
        boolean flag = rateLimiter.tryAcquire(time, TimeUnit.MILLISECONDS);
        if(!flag){
            fullBack();
            return null;
        }
        return proceedingJoinPoint.proceed();
    }

    public void fullBack(){
        response.setHeader("Content-type","text/html;charset=utf8");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.write("请求繁忙，请稍后再试！");
            pw.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(pw!=null){
                pw.close();
            }
        }
    }
}
