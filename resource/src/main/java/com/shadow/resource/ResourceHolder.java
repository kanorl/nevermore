package com.shadow.resource;

import com.google.common.base.Preconditions;
import com.shadow.event.EventBus;
import com.shadow.resource.annotation.Id;
import com.shadow.resource.event.ResourceRefreshedEvent;
import com.shadow.resource.exception.DuplicateKeyException;
import com.shadow.resource.exception.InvalidResourceException;
import com.shadow.resource.exception.ResourceNotFoundException;
import com.shadow.resource.exception.ResourcePrimaryKeyNotFoundException;
import com.shadow.resource.reader.ResourceReader;
import com.shadow.util.execution.LoggedExecution;
import com.shadow.util.execution.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author nevermore on 2015/1/13.
 */
public class ResourceHolder<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceHolder.class);

    @Autowired
    private ResourceReader resourceReader;
    @Autowired
    private EventBus eventBus;

    private Map<?, T> resources = Collections.emptyMap();
    private AtomicReference<Class<T>> typeReference = new AtomicReference<>();

    @SuppressWarnings("unchecked")
    public void initialize(Class<?> type) {
        if (!typeReference.compareAndSet(null, (Class<T>) type)) {
            throw new IllegalStateException("重复初始化");
        }
        load();
    }

    @Nonnull
    public T get(@Nonnull Object id) {
        return find(id).orElseThrow(() -> new ResourceNotFoundException("id=" + id + ", class=" + typeReference.get().getSimpleName()));
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

    public void reload() {
        LoggedExecution.forName("重新加载资源{}", typeReference.get().getSimpleName()).logLevel(LogLevel.ERROR).execute(this::load);
    }

    @SuppressWarnings("unchecked")
    private synchronized void load() {
        LoggedExecution.forName("加载资源{}", typeReference.get().getSimpleName()).execute(() -> {
            Class<T> resourceType = typeReference.get();
            List<T> resourceBeans = resourceReader.read(resourceType);
            if (Validatable.class.isAssignableFrom(resourceType)) {
                resourceBeans.forEach(bean -> Preconditions.checkState(((Validatable) bean).isValid(), new InvalidResourceException(bean)));
            }
            Field idField = Arrays.stream(resourceType.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Id.class)).findFirst().orElseThrow(() -> new ResourcePrimaryKeyNotFoundException(resourceType));
            ReflectionUtils.makeAccessible(idField);

            Map<Object, T> resources;
            if (Comparable.class.isAssignableFrom(resourceType)) {
                Collections.sort(resourceBeans, (o1, o2) -> ((Comparable) o1).compareTo(((Comparable) o2)));
                resources = new LinkedHashMap<>();
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

            this.resources = resources;

            eventBus.post(ResourceRefreshedEvent.valueOf(resourceType));
        });
    }
}
