package com.shadow.common.util.lang;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @author nevermore on 2015/1/23
 */
public class PackageScanner {

    private static final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
    private static final String RESOURCE_PATTERN = "/**/*.class";

    public static Set<Class<?>> scan(@Nonnull String... packagesToScan) {
        requireNonNull(packagesToScan);
        try {
            return Arrays.stream(packagesToScan)
                    .flatMap(PackageScanner::scan)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<?>> scan(@Nonnull String packageToScan, Predicate<Class<?>> predicate) {
        return scan(packageToScan).filter(predicate).collect(Collectors.toSet());
    }

    public static Stream<Class<?>> scan(String packageToScan) {
        requireNonNull(packageToScan);
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(packageToScan) + RESOURCE_PATTERN;
        try {
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            return Arrays.stream(resources)
                    .filter(Resource::isReadable)
                    .map(PackageScanner::loadClass);
        } catch (Exception e) {
            throw new RuntimeException("类扫描异常：package=" + packageToScan, e);
        }
    }

    private static Class<?> loadClass(Resource resource) {
        try {
            return resourcePatternResolver.getClassLoader().loadClass(readerFactory.getMetadataReader(resource)
                    .getClassMetadata().getClassName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
