package com.shadow.entity.db.mongo;

import com.google.common.collect.Range;
import com.shadow.entity.IEntity;
import com.shadow.entity.db.Repository;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author nevermore on 2015/5/2.
 */
@org.springframework.stereotype.Repository
@SuppressWarnings("unchecked")
public class MongoRepository implements Repository {

    private static final String ID_FIELD = "_id";

    @Autowired
    private MongoDataStore ds;

    @Nonnull
    @Override
    public <K extends Serializable & Comparable<K>, V extends IEntity<K>> Optional<K> getMaxId(@Nonnull Class<V> clazz, @Nonnull Range<K> range) {
        Query<V> q = ds.createQuery(clazz);
        q.field(ID_FIELD).greaterThanOrEq(range.lowerEndpoint()).field(ID_FIELD).lessThanOrEq(range.upperEndpoint());
        Key<?> key = q.order("-" + ID_FIELD).limit(1).getKey();
        return key == null ? Optional.<K>empty() : Optional.ofNullable((K) key.getId());
    }

    @Nonnull
    @Override
    public <K extends Serializable, V extends IEntity<K>> List<K> getIds(@Nonnull Class<V> clazz, @Nonnull String field, Object value) {
        return ds.find(clazz, field, value).asKeyList().stream().map(key -> (K) key.getId()).collect(Collectors.toList());
    }

    @Override
    public <K extends Serializable, V extends IEntity<K>> List<V> query(@Nonnull Class<V> clazz, @Nonnull String where) {
        return ds.createQuery(clazz).where(where).asList();
    }

    @Nullable
    @Override
    public <K extends Serializable, V extends IEntity<K>> V get(@Nonnull K id, @Nonnull Class<V> clazz) {
        return ds.get(clazz, id);
    }

    @Override
    public <K extends Serializable, V extends IEntity<K>> void save(@Nonnull V v) {
        ds.save(v);
    }

    @Override
    public <V extends IEntity<?>> void update(@Nonnull V v) {
        ds.save(v);
    }

    @Override
    public <V extends IEntity<?>> void delete(@Nonnull V v) {
        ds.delete(v);
    }
}
