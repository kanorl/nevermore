package com.shadow.test.module.player.service;

import com.mongodb.client.model.Projections;
import com.shadow.common.exception.OperationFailedException;
import com.shadow.common.injection.Injected;
import com.shadow.entity.cache.EntityCache;
import com.shadow.entity.db.mongo.MongoDataStore;
import com.shadow.test.module.player.entity.Player;
import com.shadow.test.module.player.exception.PlayerException;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import static com.shadow.test.module.player.exception.PlayerExceptionCode.PLAYER_NAME_EXISTS;

/**
 * @author nevermore on 2015/3/2
 */
@Component
public class PlayerManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerManager.class);

    @Autowired
    private MongoDataStore ds;

    @Injected
    private EntityCache<Long, Player> playerCache;

    private ConcurrentMap<String, Long> name2Id = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        ds.getMongoCollection(Player.class).find().projection(Projections.include("_id", "name")).forEach((Consumer<Document>) document -> {
            Long prev = name2Id.put(document.get("name", String.class), document.get("_id", Long.class));
            assert prev == null;
        });
    }

    Player create(Long id, String playerName, Gender gender, Country country) {
        if (name2Id.putIfAbsent(playerName, id) != null) {
            throw new PlayerException(PLAYER_NAME_EXISTS);
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
