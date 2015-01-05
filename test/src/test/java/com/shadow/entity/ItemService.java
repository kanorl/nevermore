package com.shadow.entity;

import com.shadow.entity.cache.IndexedEntityCache;
import com.shadow.entity.cache.annotation.Inject;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author nevermore on 2015/1/5
 */
@Component
public class ItemService {

    @Inject
    private IndexedEntityCache<Integer, Item> cache;

    public void add(int id) {
        cache.create(Item.valueOf(id));
    }

    public Collection<Item> getMyItems(long playerId) {
        return cache.getAll("playerId", playerId);
    }

    public Collection<Item> getEmptyItems() {
        return cache.getAll("count", 0);
    }
}
