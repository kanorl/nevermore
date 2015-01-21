package com.shadow.util.execution;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author nevermore on 2015/1/21
 */
@Component
@Aspect
public class ExecuteWithLogAspect implements Ordered {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteWithLogAspect.class);

    @Around("@annotation(com.shadow.util.execution.ExecuteWithLog)")
    public Object execute(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        ExecuteWithLog anno = method.getAnnotation(ExecuteWithLog.class);
        return LoggedExecution.forName(anno.taskName()).logLevel(anno.logLevel()).execute(() -> {
            try {
                return pjp.proceed(pjp.getArgs());
            } catch (Throwable throwable) {
                LOGGER.error("[{}] 任务执行失败：{}", anno.taskName(), throwable.getMessage());
                throw new RuntimeException(throwable);
            }
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
