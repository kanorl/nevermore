package com.shadow.entity.lock;

import com.google.common.collect.Maps;
import com.shadow.entity.lock.annotation.LockTarget;
import com.shadow.entity.lock.exception.IllegalAutoLockedMethodException;
import com.shadow.entity.lock.exception.IllegalLockTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 自动加锁的方法
 *
 * @author nevermore on 2014/11/27.
 */
class AutoLockedMethod {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoLockedMethod.class);


    private final Map<Integer, LockTarget> lockTargets;

    public AutoLockedMethod(Method method) {
        Parameter[] parameters = method.getParameters();
        lockTargets = Maps.newHashMapWithExpectedSize(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            LockTarget lockTarget = parameter.getAnnotation(LockTarget.class);
            if (lockTarget == null) {
                continue;
            }
            lockTargets.put(i, validate(lockTarget, parameter, method));
        }

        if (lockTargets.isEmpty()) {
            throw new IllegalAutoLockedMethodException("自动加锁方法没有注解为@LockTarget的参数: " + detail(method));
        }
    }

    /**
     * 验证注解是否被正确使用
     *
     * @param lockTarget
     * @param parameter
     * @param method
     * @return
     */
    private LockTarget validate(final LockTarget lockTarget, Parameter parameter, Method method) {
        Class<?> pType = parameter.getType();
        boolean isCollection = Collection.class.isAssignableFrom(pType);
        boolean isArray = pType.isArray();
        if (lockTarget.value() == LockTarget.Type.Element) {
            if (!isCollection && !isArray) {
                throw new IllegalLockTargetException("注解@AutoLocked(LockTarget.Type.Element)使用错误，目标必须为集合或者数组: " + detail(method));
            }
            if (isArray && pType.getComponentType().isPrimitive()) {
                throw new IllegalLockTargetException("无法对一个基本类型的数组的元素加锁: " + detail(method));
            }
        } else {
            if (pType.isPrimitive()) {
                throw new IllegalLockTargetException("无法对一个基本类型加锁: " + detail(method));
            }
            if (isCollection || isArray && LOGGER.isWarnEnabled()) {
                LOGGER.warn("如果要对一个集合或数组中的元素加锁，请使用@LockTarget(LockTarget.Type.Element)注解: " + detail(method));
            }
        }
        return lockTarget;
    }

    public Object[] extractLockTargets(Object[] args) throws IllegalLockTargetException {
        List<Object> lockObjects = new ArrayList<>();
        for (Map.Entry<Integer, LockTarget> entry : lockTargets.entrySet()) {
            Object arg = args[entry.getKey()];
            Collection<?> targets = entry.getValue().value().extract(arg);
            lockObjects.addAll(targets);
        }
        return lockObjects.toArray();
    }

    private String detail(Method method) {
        return "class=" + method.getDeclaringClass().getName() + " method=" + method.getName();
    }
}
