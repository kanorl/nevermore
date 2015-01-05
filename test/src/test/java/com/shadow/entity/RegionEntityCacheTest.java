package com.shadow.entity;

import com.shadow.entity.cache.EntityCacheManager;
import com.shadow.entity.cache.RegionEntityCache;
import com.shadow.entity.cache.RegionEntityCacheImpl;
import com.shadow.entity.orm.DataAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author nevermore on 2015/1/5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class RegionEntityCacheTest {

    @Autowired
    private EntityCacheManager entityCacheManager;
    @Autowired
    private DataAccessor dataAccessor;
    @Autowired
    private ItemService itemService;

    @Before
    public void before() {
        for (int i = 0; i < 10; i++) {
            itemService.add(i);
        }
    }

    @Test
    public void test() {
        RegionEntityCache<Integer, Item> cache = new RegionEntityCacheImpl<>(Item.class, dataAccessor, entityCacheManager.getPersistenceProcessor(Item.class));
        List<Item> items = cache.getAll("playerId", 0L);
        System.err.println(items);
    }
}
