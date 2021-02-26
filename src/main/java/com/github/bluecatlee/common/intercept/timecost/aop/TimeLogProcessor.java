package com.github.bluecatlee.common.intercept.timecost.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@EnableAspectJAutoProxy
public class TimeLogProcessor {

    @Around("@annotation(com.github.bluecatlee.common.intercept.timecost.aop.TimeLog)")
    public void a(ProceedingJoinPoint joinPoint) {

        long start = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        long end = System.currentTimeMillis();
        long timeUse = end - start;

        String name = joinPoint.getTarget().getClass().getName();
        String name1 = joinPoint.getSignature().getName();
        if(timeUse > 30 * 1000L) {
            log.warn("{}.{} execute time {} seconds", name, name1, timeUse);
        }else{
            log.info("{}.{} execute time {} seconds", name, name1, timeUse);
        }
    }

}
