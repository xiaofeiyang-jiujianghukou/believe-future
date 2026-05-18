package com.believe.common.log.aspect;

import com.believe.common.log.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;

@Slf4j
@Aspect
public class LogAspect {

    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String description = logAnnotation.value().isEmpty() ? signature.toShortString() : logAnnotation.value();

        if (logAnnotation.printArgs()) {
            log.info(">> {} args: {}", description, Arrays.toString(joinPoint.getArgs()));
        } else {
            log.info(">> {}", description);
        }

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;

            if (logAnnotation.printResult()) {
                log.info("<< {} result: {} ({}ms)", description, result, elapsed);
            } else {
                log.info("<< {} ({}ms)", description, elapsed);
            }
            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("<< {} failed after {}ms: {}", description, elapsed, e.getMessage());
            throw e;
        }
    }
}
