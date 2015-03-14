package com.shadow.resource;

import com.google.common.collect.Iterables;
import com.shadow.resource.annotation.Id;
import com.shadow.resource.exception.DuplicateKeyException;
import com.shadow.resource.exception.InvalidResourceException;
import com.shadow.resource.exception.ResourceNotFoundException;
import com.shadow.resource.exception.ResourcePrimaryKeyNotFoundException;
import com.shadow.resource.reader.ResourceReader;
import com.shadow.util.execution.LogLevel;
import com.shadow.util.execution.LoggedExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author nevermore on 2015/1/13.
 */
public class ResourceHolder<T> {

    @Autowired
    private ResourceReader resourceReader;
    private Map<Object, T> resources = Collections.emptyMap();
    private volatile Class<T> resourceType;
    private volatile int rateTotal;

    @SuppressWarnings("unchecked")
    public void initialize(Class<?> type) {
        assert resourceType == null;

        resourceType = (Class<T>) type;
        load();
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

    @Nonnull
    public Optional<T> findFirst(@Nonnull Predicate<T> predicate) {
        return resources.values().stream().filter(predicate).findFirst();
    }

    public T getFirst(@Nonnull Predicate<T> predicate) {
        return findFirst(predicate).orElseThrow(ResourceNotFoundException::new);
    }

    public T min() {
        if (resources.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        if (!(resources instanceof NavigableMap)) {
            throw new UnsupportedOperationException("Resource is not comparable.");
        }
        return ((NavigableMap<Object, T>) resources).firstEntry().getValue();
    }

    public T max() {
        return navigableResources().lastEntry().getValue();
    }

    public Optional<T> previous(Object currentKey) {
        Map.Entry<Object, T> lowerEntry = navigableResources().lowerEntry(currentKey);
        return lowerEntry == null ? Optional.<T>empty() : Optional.ofNullable(lowerEntry.getValue());
    }

    public Optional<T> next(Object currentKey) {
        Map.Entry<Object, T> higherEntry = navigableResources().higherEntry(currentKey);
        return higherEntry == null ? Optional.<T>empty() : Optional.ofNullable(higherEntry.getValue());
    }

    public T random() {
        if (resources.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        if (!Randomable.class.isAssignableFrom(resourceType)) {
            throw new UnsupportedOperationException("Resource is not Randomized.");
        }
        int r = ThreadLocalRandom.current().nextInt(rateTotal);
        for (T t : resources.values()) {
            Randomable randomable = (Randomable) t;
            if (randomable.getRate() > r) {
                return t;
            }
            r -= randomable.getRate();
        }
        int randomIndex = ThreadLocalRandom.current().nextInt(resources.values().size());
        return Iterables.get(resources.values(), randomIndex);
    }

    private NavigableMap<Object, T> navigableResources() {
        if (resources.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        if (!(resources instanceof NavigableMap)) {
            throw new UnsupportedOperationException("Resource is not comparable.");
        }
        return (NavigableMap<Object, T>) resources;
    }

    void reload() {
        LoggedExecution.forName("重新加载资源{}", resourceType.getSimpleName()).logLevel(LogLevel.ERROR).execute(this::load);
    }

    @SuppressWarnings("unchecked")
    private synchronized void load() {
        LoggedExecution.forName("加载资源{}", resourceType.getSimpleName()).execute(() -> {
            List<T> resourceBeans = resourceReader.read(resourceType);
            if (Validatable.class.isAssignableFrom(resourceType)) {
                resourceBeans.stream().filter(bean -> !((Validatable) bean).isValid()).findAny().ifPresent(bean -> {
                    throw new InvalidResourceException(bean);
                });
            }
            Field idField = Arrays.stream(resourceType.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Id.class)).findFirst().orElseThrow(() -> new ResourcePrimaryKeyNotFoundException(resourceType));
            ReflectionUtils.makeAccessible(idField);

            Map<Object, T> resources;
            if (Comparable.class.isAssignableFrom(resourceType)) {
                Collections.sort(resourceBeans, (o1, o2) -> ((Comparable<T>) o1).compareTo(o2));
                resources = new TreeMap<>();
            } else {
                resources = new HashMap<>();
            }
            resourceBeans.forEach(bean -> {
                try {
                    Object key = idField.get(bean);
                    if (resources.putIfAbsent(key, bean) != null) {
                        throw new DuplicateKeyException(resourceType, key);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

            if (Randomable.class.isAssignableFrom(resourceType)) {
                rateTotal = resourceBeans.stream().reduce(0, (rate, obj) -> rate + ((Randomable) obj).getRate(),
                        (rate1, rate2) -> rate1 + rate2);
            }

            this.resources = resources;
        });
    }
}
