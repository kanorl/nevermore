package com.shadow.entity;

import com.shadow.entity.cache.IndexEntry;
import com.shadow.entity.cache.IndexedEntityCache;
import com.shadow.entity.cache.annotation.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

/**
 * @author nevermore on 2015/1/5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class IndexedEntityCacheTest {

    @Inject
    private IndexedEntityCache<Integer, Item> entityCache;

    @Before
    public void before() {

    }

    @Test
    public void test() {
        Collection<Item> items = entityCache.getAll(IndexEntry.valueOf("playerId", 0L));
        System.out.println(items.size());


        Item item = entityCache.get(0);
        if (item != null) {
            item.setPlayerId(1);
            entityCache.updateWithIndexChanged(item, IndexEntry.valueOf("playerId", 0L));
        }

        Collection<Item> items2 = entityCache.getAll(IndexEntry.valueOf("playerId", 0L));
        System.out.println(items2.size());

        Collection<Item> items3 = entityCache.getAll(IndexEntry.valueOf("playerId", 1L));
        System.out.println(items3.size());
    }
}
