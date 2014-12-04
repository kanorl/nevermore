package com.shadow.entity.lock.annotation;


import com.shadow.entity.lock.IllegalLockTargetException;

import javax.annotation.Nonnull;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author nevermore on 2014/11/27.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LockTarget {

    public Type value() default Type.Object;

    public enum Type {
        Object {
            @Override
            public Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException {
                return Collections.singletonList(checkNotNull(arg, "Cannot lock a null element."));
            }
        },
        Element {
            @Override
            public Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException {
                checkNotNull(arg, "Cannot lock a null element.");

                Stream<?> stream;
                if (arg instanceof Collection<?>) {
                    stream = ((Collection<?>) arg).stream();
                } else if (arg.getClass().isArray()) {
                    stream = Arrays.stream((Object[]) arg);
                } else {
                    throw new IllegalLockTargetException("Unsupported lock target type.(should never happen)");
                }
                stream.forEach((e) -> checkNotNull(e, "Cannot lock a null element."));
                return stream.collect(Collectors.toList());
            }
        };

        public abstract Collection<?> extract(@Nonnull Object arg) throws IllegalLockTargetException;
    }
}
