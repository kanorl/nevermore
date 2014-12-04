package com.shadow.entity.lock;

import com.google.common.collect.Maps;
import com.shadow.entity.lock.annotation.LockTarget;

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
public class AutoLockedMethod {

    private final Map<Integer, LockTarget> lockTargets;

    public AutoLockedMethod(Method method) throws IllegalLockTargetException {
        Parameter[] parameters = method.getParameters();
        lockTargets = Maps.newHashMapWithExpectedSize(parameters.length);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            LockTarget lockTarget = parameter.getAnnotation(LockTarget.class);
            if (lockTarget == null) {
                continue;
            }
            lockTargets.put(i, validate(lockTarget, parameter));
        }

        if (lockTargets.isEmpty()) {
            throw new IllegalLockTargetException("A method annotated by @AutoLocked must have at least one arg annotated by @LockTarget.");
        }
    }

    /**
     * 验证注解是否被正确使用
     *
     * @param lockTarget
     * @param parameter
     * @return
     * @throws IllegalLockTargetException
     */
    private LockTarget validate(final LockTarget lockTarget, Parameter parameter) throws IllegalLockTargetException {
        Class<?> pType = parameter.getType();
        if (lockTarget.value() == LockTarget.Type.Element) {
            boolean isCollection = Collection.class.isAssignableFrom(pType);
            boolean isArray = pType.isArray();
            if (!isCollection && !isArray) {
                throw new IllegalLockTargetException("A parameter annotated by @LockTarget(LockTarget.Type.Element) whose type must be Collection or Array.");
            }
            if (isArray && pType.getComponentType().isPrimitive()) {
                throw new IllegalLockTargetException("Cannot lock a primitive array.");
            }
        } else {
            if (pType.isPrimitive()) {
                throw new IllegalLockTargetException("Cannot lock a primitive type.");
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
}
