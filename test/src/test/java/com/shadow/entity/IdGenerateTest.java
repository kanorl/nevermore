package com.shadow.entity;

import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.cache.RegionEntityCache;
import com.shadow.entity.id.EntityIdGenerator;
import com.shadow.test.module.player.entity.Player;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import com.shadow.util.codec.JsonUtil;
import com.shadow.util.config.ServerConfig;
import com.shadow.util.injection.Injected;
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
    @Injected
    private RegionEntityCache<Long, Item> itemCache;
    @Injected
    private EntityCache<Long, Player> playerCache;
    @Autowired
    private EntityIdGenerator entityIdGenerator;
    @Autowired
    private ServerConfig serverConfig;


    @Test
    public void test() {
//        short server = serverProperty.getServers().get(0);
//        long playerId = idGenerator.next(Player.class, server);
        long playerId = 3057944421862473729L;
        Player player = playerCache.getOrCreate(playerId, () -> Player.valueOf(playerId, "playerName", Gender.MALE, Country.ONE));


        long itemId = entityIdGenerator.next(Item.class, playerId);
//        long itemId = 3057944421862473729L;
        Item item = itemCache.create(Item.valueOf(itemId, playerId));

        System.out.println(JsonUtil.toJson(player));
        System.out.println(JsonUtil.toJson(item));

        System.out.println(itemCache.list(playerId));
    }
}
