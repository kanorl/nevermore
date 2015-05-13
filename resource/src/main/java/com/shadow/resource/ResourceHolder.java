package com.shadow.resource;

import com.shadow.common.util.RandomUtil;
import com.shadow.common.util.execution.LogLevel;
import com.shadow.common.util.execution.LoggedExecution;
import com.shadow.resource.annotation.Id;
import com.shadow.resource.exception.DuplicateKeyException;
import com.shadow.resource.exception.InvalidResourceException;
import com.shadow.resource.exception.ResourceNotFoundException;
import com.shadow.resource.exception.ResourcePrimaryKeyNotFoundException;
import com.shadow.resource.reader.ResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author nevermore on 2015/1/13.
 */
public class ResourceHolder<T> {

    @Autowired
    private ResourceReader resourceReader;
    private Map<Object, T> resources = Collections.emptyMap();
    private Class<T> resourceType;

    @SuppressWarnings("unchecked")
    synchronized void initialize(Class<?> type) {
        resourceType = (Class<T>) type;
        load();
    }

    @SuppressWarnings("unchecked")
    private synchronized void load() {
        LoggedExecution.forName("加载资源{}", resourceType.getSimpleName())
                .execute(() -> {
                    List<T> resourceBeans = resourceReader.read(resourceType);
                    if (Validatable.class.isAssignableFrom(resourceType)) {
                        resourceBeans.stream().filter(bean -> !((Validatable) bean).isValid()).findAny().ifPresent(bean -> {
                            throw new InvalidResourceException(bean);
                        });
                    }
                    Field idField = Arrays.stream(resourceType.getDeclaredFields()).filter(field -> field.isAnnotationPresent
                            (Id.class)).findFirst().orElseThrow(() -> new ResourcePrimaryKeyNotFoundException(resourceType));
                    ReflectionUtils.makeAccessible(idField);

                    Map<Object, T> resources = new HashMap<>();
                    resourceBeans.forEach(bean -> {
                        Object key = ReflectionUtils.getField(idField, bean);
                        if (resources.putIfAbsent(key, bean) != null) {
                            throw new DuplicateKeyException(resourceType, key);
                        }
                    });

                    this.resources = resources;

                    if (Comparable.class.isAssignableFrom(resourceType)) {
                        Map<Object, T> sortedMap = new TreeMap<>((k1, k2) -> {
                            T t1 = resources.get(k1);
                            T t2 = resources.get(k2);
                            return ((Comparable) t1).compareTo(t2);
                        });
                        sortedMap.putAll(resources);
                        this.resources = sortedMap;
                    }
                });
    }

    @Nonnull
    public T get(@Nonnull Object id) {
        return find(id).orElseThrow(() -> new ResourceNotFoundException("id=" + id + ", class=" + resourceType.getSimpleName()));
    }

    public Optional<T> find(@Nonnull Object id) {
        return Optional.ofNullable(resources.get(id));
    }

    @Nonnull
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(resources.values());
    }

    @Nonnull
    public List<T> findAll(@Nonnull Predicate<T> predicate) {
        return resources.values().stream().filter(predicate).collect(Collectors.toList());

    }

    public T getFirst(@Nonnull Predicate<T> predicate) {
        return findFirst(predicate).orElseThrow(ResourceNotFoundException::new);
    }

    @Nonnull
    public Optional<T> findFirst(@Nonnull Predicate<T> predicate) {
        return resources.values().stream().filter(predicate).findFirst();
    }

    @Nonnull
    public T min() {
        return findMin().orElseThrow(ResourceNotFoundException::new);
    }

    @Nonnull
    public Optional<T> findMin() {
        return extractValue(navigableResources().firstEntry());
    }

    private Optional<T> extractValue(Map.Entry<Object, T> entry) {
        return entry == null ? Optional.<T>empty() : Optional.ofNullable(entry.getValue());
    }

    private NavigableMap<Object, T> navigableResources() {
        if (!(resources instanceof NavigableMap)) {
            throw new UnsupportedOperationException("Resource is not comparable.");
        }
        return (NavigableMap<Object, T>) resources;
    }

    @Nonnull
    public T max() {
        return findMax().orElseThrow(ResourceNotFoundException::new);
    }

    @Nonnull
    public Optional<T> findMax() {
        return extractValue(navigableResources().lastEntry());
    }

    @Nonnull
    public Optional<T> previous(Object currentKey) {
        return extractValue(navigableResources().lowerEntry(currentKey));
    }

    @Nonnull
    public Optional<T> next(Object currentKey) {
        return extractValue(navigableResources().higherEntry(currentKey));
    }

    @Nonnull
    public T random() {
        if (!Randomable.class.isAssignableFrom(resourceType)) {
            throw new UnsupportedOperationException("Resource is not " + Randomable.class.getSimpleName());
        }
        return RandomUtil.random(resources.values(), t -> ((Randomable) t).getRate()).orElseThrow
                (ResourceNotFoundException::new);
    }

    @Nonnull
    public T random(@Nonnull Predicate<T> predicate) {
        if (!Randomable.class.isAssignableFrom(resourceType)) {
            throw new UnsupportedOperationException("Resource is not " + Randomable.class.getSimpleName());
        }
        return RandomUtil.random(resources.values(), t -> ((Randomable) t).getRate(), predicate).orElseThrow
                (ResourceNotFoundException::new);
    }

    synchronized void reload() {
        LoggedExecution.forName("重新加载资源{}", resourceType.getSimpleName()).logLevel(LogLevel.ERROR).execute(this::load);
    }
}
