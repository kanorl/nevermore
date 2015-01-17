package com.shadow.entity;

import com.shadow.entity.cache.RegionEntityCache;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.util.injection.Injected;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author nevermore on 2015/1/5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class RegionEntityCacheTest {

    @Injected
    private RegionEntityCache<Long, Item> entityCache;
    @Autowired
    private DataAccessor dataAccessor;

    @Before
    public void before() {

    }

    @Test
    public void test() {
//        Collection<Item> items = entityCache.list(0L);
//        System.out.println(items.size());
//
//
//        Item item = entityCache.get(0);
//        if (item != null) {
//            item.increase();
//        }
//
//        Collection<Item> items2 = entityCache.list(0L);
//        System.out.println(items2.size());
//
//        Collection<Item> items3 = entityCache.list(1L);
//        System.out.println(items3.size());
//
//        Item i = entityCache.getOrCreate(1, () -> Item.valueOf(1));
//        System.out.println(i);
    }
}
