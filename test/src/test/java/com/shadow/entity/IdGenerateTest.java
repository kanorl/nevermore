package com.shadow.entity;

import com.shadow.entity.annotation.Inject;
import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.cache.RegionEntityCache;
import com.shadow.entity.id.IdGenerator;
import com.shadow.util.codec.JsonUtil;
import com.shadow.util.config.ServerProperty;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author nevermore on 2015/1/10
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class IdGenerateTest {
    @Inject
    private RegionEntityCache<Long, Item> itemCache;
    @Inject
    private EntityCache<Long, Player> playerCache;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private ServerProperty serverProperty;


    @Test
    public void test() {
//        short server = serverProperty.getServers().get(0);
//        long playerId = idGenerator.next(Player.class, server);
        long playerId = 3057944421862473729L;
        Player player = playerCache.getOrCreate(playerId, () -> Player.valueOf(playerId));


        long itemId = idGenerator.next(Item.class, playerId);
//        long itemId = 3057944421862473729L;
        Item item = itemCache.create(Item.valueOf(itemId, playerId));

        System.out.println(JsonUtil.toJson(player));
        System.out.println(JsonUtil.toJson(item));

        System.out.println(itemCache.list(playerId));
    }
}
