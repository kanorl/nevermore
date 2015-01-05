package com.shadow.entity;

import com.shadow.entity.cache.RegionEntityCache;
import com.shadow.entity.cache.annotation.Inject;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/1/5
 */
@Component
public class ItemService {

    @Inject
    private RegionEntityCache<Integer, Item> cache;

    public void add(int id) {
        cache.create(Item.valueOf(id));
    }
}
