package com.shadow.entity.lock.annotation;


import com.shadow.entity.lock.exception.IllegalLockTargetException;

import javax.annotation.Nonnull;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 对注解为@AutoLocked方法的参数使用
 * 表示对该参数进行自动加锁
 *
 * @author nevermore on 2014/11/27.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LockTarget {

    public Type value() default Type.Object;

    /**
     * 加锁对象的类型
     */
    public enum Type {

        /**
         * 默认的加锁对象类型
         */
        Object {
            @Override
            public Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException {
                return Collections.singletonList(checkNotNull(arg));
            }
        },

        /**
         * 仅适用于加锁对象为集合或数组时
         * 表示对该集合或数据中的元素进行加锁
         */
        Element {
            @SuppressWarnings("Convert2MethodRef")
            @Override
            public Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException {
                checkNotNull(arg);

                Stream<?> stream;
                if (arg instanceof Collection<?>) {
                    stream = ((Collection<?>) arg).stream();
                } else if (arg.getClass().isArray()) {
                    stream = Arrays.stream((Object[]) arg);
                } else {
                    throw new IllegalLockTargetException("Unsupported lock target type.(should never happen)");
                }
                stream.forEach((e) -> checkNotNull(e));
                return stream.collect(Collectors.toList());
            }
        };

        public abstract Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException;

        protected <T> T checkNotNull(T t) {
            if (t == null) {
                throw new IllegalLockTargetException("加锁对象不能为null。");
            }
            return t;
        }
    }
}
