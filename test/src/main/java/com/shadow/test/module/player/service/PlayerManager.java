package com.shadow.test.module.player.service;

import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.orm.DataAccessor;
import com.shadow.test.module.player.entity.Player;
import com.shadow.test.module.player.exception.PlayerNameExistsException;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import com.shadow.util.exception.OperationFailedException;
import com.shadow.util.injection.Injected;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author nevermore on 2015/3/2
 */
@Component
public class PlayerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);

    @Autowired
    private DataAccessor dataAccessor;

    @Injected
    private EntityCache<Long, Player> playerCache;

    private ConcurrentMap<String, Long> name2Id;

    @PostConstruct
    private void init() {
        List<Object[]> result = dataAccessor.namedQuery(Player.QUERY_NAME_AND_ID);
        name2Id = new ConcurrentHashMap<>(result.size());
        result.forEach(e -> name2Id.put((String) e[0], (Long) e[1]));
    }

    Player create(Long id, String playerName, Gender gender, Country country) {
        if (name2Id.putIfAbsent(playerName, id) != null) {
            throw new PlayerNameExistsException();
        }
        try {
            return playerCache.create(Player.valueOf(id, playerName, gender, country));
        } catch (Exception e) {
            name2Id.remove(playerName, id);
            LOGGER.error("创建角色失败", e);
            throw new OperationFailedException();
        }
    }

    Optional<Player> get(Long id) {
        return playerCache.get(id);
    }
}
