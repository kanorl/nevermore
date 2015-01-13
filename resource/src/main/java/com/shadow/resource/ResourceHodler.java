package com.shadow.resource;

import java.util.*;

/**
 * @author nevermore on 2015/1/13.
 */
public class ResourceHodler<K, V> {

    private Map<K, V> resources = Collections.emptyMap();
    private Map<String, Map<String, Set<K>>> indexedResources = Collections.emptyMap();

    public V get(K id) {
        return resources.get(id);
    }

    public Collection<V> getAll() {
        return Collections.unmodifiableCollection(resources.values());
    }

    public List<V> list(String indexName, Object... indexValues) {
        Objects.requireNonNull(indexValues);
        Map<String, Set<K>> values = indexedResources.get(indexName);
        if (values == null) {
            throw new RuntimeException();
        }
        String key = (String) Arrays.stream(indexValues).reduce("", (o1, o2) -> o1 + "_" + o2);
        return values.get(key).stream().collect(ArrayList::new, (vs, k) -> vs.add(get(k)), List::addAll);
    }

    public V unique(String indexName, Object... indexValues) {
        return list(indexName, indexValues).stream().findFirst().get();
    }
}
