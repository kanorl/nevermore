package com.shadow.entity;

import com.shadow.entity.cache.EntityCacheManager;
import com.shadow.entity.orm.DataAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

/**
 * @author nevermore on 2015/1/5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class IndexedEntityCacheTest {

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
        Collection<Item> items = itemService.getMyItems(0L);
        System.err.println(items.toString());

        Collection<Item> items2 = itemService.getEmptyItems();

        for (Item item : items2) {
            if (items.contains(item)) {
                System.err.println(item.getId());
            }
        }
    }
}
