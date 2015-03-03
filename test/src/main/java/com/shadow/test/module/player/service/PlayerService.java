package com.shadow.test.module.player.service;

import com.shadow.event.EventBus;
import com.shadow.test.module.player.entity.Player;
import com.shadow.test.module.player.event.PlayerCreateEvent;
import com.shadow.test.module.player.model.Country;
import com.shadow.test.module.player.model.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author nevermore on 2015/3/2
 */
@Component
public class PlayerService {

    @Autowired
    private PlayerManager playerManager;
    @Autowired
    private EventBus eventBus;

    public Player create(Long id, String playerName, Gender gender, Country country) {
        Player player = playerManager.create(id, playerName, gender, country);
        eventBus.post(PlayerCreateEvent.valueOf(player));
        return player;
    }

    public Optional<Player> getPlayer(Long id) {
        return playerManager.get(id);
    }
}
