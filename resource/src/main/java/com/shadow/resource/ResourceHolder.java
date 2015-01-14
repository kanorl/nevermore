package com.shadow.resource;

import com.shadow.resource.exception.NoSuchIndexException;
import com.shadow.resource.exception.ResourceNotFoundException;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author nevermore on 2015/1/13.
 */
public class ResourceHolder<V> {

    private Map<?, V> resources = Collections.emptyMap();
    private Map<String, Map<String, List<?>>> indexedResources = Collections.emptyMap();
    private Class<V> clazz;

    @Nonnull
    public V get(@Nonnull Object id) {
        V v = resources.get(id);
        if (v == null) {
            throw new ResourceNotFoundException("id=" + id + ", class=" + clazz.getSimpleName());
        }
        return v;
    }

    @Nonnull
    public Collection<V> getAll() {
        return Collections.unmodifiableCollection(resources.values());
    }

    @Nonnull
    public List<V> list(@Nonnull String indexName, @Nonnull Object... indexValues) {
        Objects.requireNonNull(indexValues);
        Map<String, List<?>> values = indexedResources.get(indexName);
        if (values == null) {
            throw new NoSuchIndexException(indexName + " in " + clazz.getName());
        }
        String key = (String) Arrays.stream(indexValues).reduce("", (o1, o2) -> o1 + "_" + o2);
        return values.get(key).stream().collect(ArrayList::new, (vs, k) -> vs.add(get(k)), List::addAll);
    }

    public V unique(String indexName, Object... indexValues) {
        Optional<V> optional = list(indexName, indexValues).stream().findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new ResourceNotFoundException("index=" + indexName + ", values=" + Arrays.toString(indexValues) + " in " + clazz.getName());
    }

    public void reload() {
        load();
    }

    private void load() {

    }
}
