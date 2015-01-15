package com.shadow.resource;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.shadow.event.EventBus;
import com.shadow.resource.event.ResourceRefreshedEvent;
import com.shadow.resource.exception.*;
import com.shadow.resource.reader.ResourceReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author nevermore on 2015/1/13.
 */
public class ResourceHolder<T> {

    @Autowired
    private ResourceReader resourceReader;
    @Autowired
    private EventBus eventBus;

    private Map<?, T> resources = Collections.emptyMap();
    private Map<String, Map<String, List<?>>> indexedResources = Collections.emptyMap();
    private AtomicReference<Class<?>> typeReference = new AtomicReference<>();

    @SuppressWarnings("unchecked")
    public void initialize(Class<?> type) {
        if (!typeReference.compareAndSet(null, type)) {
            throw new IllegalStateException();
        }
        load();
    }

    @Nonnull
    public T get(@Nonnull Object id) {
        T t = resources.get(id);
        if (t == null) {
            throw new ResourceNotFoundException("id=" + id + ", class=" + typeReference.get().getSimpleName());
        }
        return t;
    }

    @Nonnull
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(resources.values());
    }

    @Nonnull
    public List<T> list(@Nonnull String indexName, @Nonnull Object... indexValues) {
        Objects.requireNonNull(indexValues);
        Map<String, List<?>> values = indexedResources.get(indexName);
        if (values == null) {
            throw new NoSuchIndexException(indexName + " in " + typeReference.get().getName());
        }
        String key = (String) Arrays.stream(indexValues).reduce("", (o1, o2) -> o1 + "_" + o2);
        return values.get(key).stream().collect(ArrayList::new, (vs, k) -> vs.add(get(k)), List::addAll);
    }

    public T unique(String indexName, Object... indexValues) {
        Optional<T> optional = list(indexName, indexValues).stream().findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new ResourceNotFoundException("index=" + indexName + ", values=" + Arrays.toString(indexValues) + " in " + typeReference.get().getName());
    }

    public void reload() {
        load();
    }

    @SuppressWarnings("unchecked")
    private synchronized void load() {
        Class<T> resourceType = (Class<T>) typeReference.get();
        Set<T> resourceBeans = resourceReader.read(resourceType);
        if (Validatable.class.isAssignableFrom(resourceType)) {
            resourceBeans.forEach(bean -> Preconditions.checkState(((Validatable) bean).isValid(), new InvalidResourceException(bean)));
        }
        Field idField = Arrays.stream(resourceType.getDeclaredFields()).findFirst().orElseThrow(() -> new ResourcePrimaryKeyNotFoundException(resourceType));
        ReflectionUtils.makeAccessible(idField);
        Map<?, T> map = Maps.uniqueIndex(resourceBeans, bean -> {
            try {
                return idField.get(bean);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        Map<Object, T> resources = Maps.newHashMapWithExpectedSize(map.size());
        map.forEach((k, t) -> {
            T old = resources.putIfAbsent(k, t);
            if (old != null) {
                throw new DuplicateKeyException(resourceType, k);
            }
        });
        this.resources = resources;

        eventBus.post(ResourceRefreshedEvent.valueOf(resourceType));
    }
}
