package com.shadow.util.lang;

import org.apache.commons.lang3.ArrayUtils;
import org.reflections.ReflectionUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author nevermore on 2014/11/26
 */
public final class ReflectUtil {
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    @Nonnull
    public static String[] getParamNames(@Nonnull Method method) {
        checkNotNull(method);
        String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
        return paramNames == null ? ArrayUtils.EMPTY_STRING_ARRAY : paramNames;
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getDeclaredAnnotation(@Nonnull Class<?> type, @Nonnull Class<A> annotationClass) {
        checkNotNull(type);
        checkNotNull(annotationClass);

        return (A) ReflectionUtils.getAllAnnotations(type, input -> input != null && annotationClass.isInstance(input)).stream().findFirst().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> Set<Method> getDeclaredMethodsAnnotatedBy(@Nonnull Class<?> type, @Nonnull Class<A> annotationClass) {
        checkNotNull(type);
        checkNotNull(annotationClass);

        return ReflectionUtils.getAllMethods(type, input -> input != null && input.isAnnotationPresent(annotationClass));
    }
}
