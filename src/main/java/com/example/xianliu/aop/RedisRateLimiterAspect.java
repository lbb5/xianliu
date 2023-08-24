package com.example.xianliu.aop;

import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.support.collections.DefaultRedisList;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author libingbing
 */
@Aspect
@Component
public class RedisRateLimiterAspect {


    @Autowired
    public HttpServletResponse response;
    @Autowired
    public StringRedisTemplate redisTemplate;

    private DefaultRedisScript<List> redisScript;

    @Pointcut("@annotation(com.example.xianliu.aop.RedisRateLimiter)")
    public void pointcut(){

    }
    @Around(value = "pointcut()")
    public Object process(ProceedingJoinPoint point) throws Throwable{
        MethodSignature signature = (MethodSignature)point.getSignature();
        RedisRateLimiter annotation = signature.getMethod().getDeclaredAnnotation(RedisRateLimiter.class);

        if(annotation == null){
            return point.proceed();
        }
        double value = annotation.limit();
        String key = "ip:"+System.currentTimeMillis() / 1000;
        List<String> keyList = Lists.newArrayList(key);
        List execute = redisTemplate.execute(redisScript, keyList, String.valueOf(value));
        if("0".equals(execute.get(0).toString())){
            fullBack();
            return null;
        }
        return point.proceed();
    }
    @PostConstruct
    public void init(){
        redisScript= new DefaultRedisScript<List>();
        redisScript.setResultType(List.class);
       redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("limit.lua")));
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
