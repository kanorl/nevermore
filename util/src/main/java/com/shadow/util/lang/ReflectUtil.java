package com.shadow.util.lang;

import org.apache.commons.lang3.ArrayUtils;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2014/11/26
 */
public final class ReflectUtil {
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
    private static final String RESOURCE_PATTERN = "/**/*.class";
    private static final Reflections reflections = new Reflections("");

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
    public static <A extends Annotation> Set<Method> getDeclaredMethodsAnnotatedWith(@Nonnull Class<?> type, @Nonnull Class<A> annotationClass) {
        checkNotNull(type);
        checkNotNull(annotationClass);

        return ReflectionUtils.getAllMethods(type, input -> input != null && input.isAnnotationPresent(annotationClass));
    }

    public static <T> Set<Class<? extends T>> getAllSubTypesOf(@Nonnull Class<T> clazz) {
        checkNotNull(clazz);
        return reflections.getSubTypesOf(clazz);
    }

    public static <A extends Annotation> Set<Class<?>> getTypesAnnotatedWith(@Nonnull Class<A> annotationClass) {
        requireNonNull(annotationClass);
        return reflections.getTypesAnnotatedWith(annotationClass);
    }

    public static Set<Class<?>> getAllTypes(@Nonnull String... packagesToScan) {
        requireNonNull(packagesToScan);
        try {
            Set<Class<?>> classes = new HashSet<>();
            for (String pkg : packagesToScan) {
                String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(pkg) + RESOURCE_PATTERN;
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader reader = readerFactory.getMetadataReader(resource);
                        String className = reader.getClassMetadata().getClassName();
                        Class<?> clazz = resourcePatternResolver.getClassLoader().loadClass(className);
                        classes.add(clazz);
                    }
                }
            }
            return classes;
        } catch (Exception e) {
            throw new RuntimeException("类扫描异常：packages=" + Arrays.toString(packagesToScan), e);
        }
    }

    public static Set<Class<?>> getAllTypes(@Nonnull String packageToScan, Predicate<Class<?>> predicate) {
        return getAllTypes(packageToScan).stream().filter(predicate).collect(Collectors.toSet());
    }
}
