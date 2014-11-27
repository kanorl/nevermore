package com.shadow.entity.lock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * @author nevermore on 2014/11/27.
 */
@Component
@Aspect
public class LockAspect {

    private static final LoadingCache<Method, AutoLockedMethod> METHOD_CACHE = CacheBuilder.newBuilder().concurrencyLevel(16).build(new CacheLoader<Method, AutoLockedMethod>() {
        @Override
        public AutoLockedMethod load(@Nonnull Method method) throws Exception {
            return new AutoLockedMethod(method);
        }
    });

    @Around("@annotation(com.shadow.entity.lock.annotation.AutoLocked)")
    public Object execute(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        AutoLockedMethod m = METHOD_CACHE.get(method);
        Object[] objects = m.extractLockTargets(pjp.getArgs());
        LockChain lockChain = LockChain.build(objects);
        lockChain.lock();
        try {
            return pjp.proceed(pjp.getArgs());
        } finally {
            lockChain.unlock();
        }
    }
}
