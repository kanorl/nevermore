package com.shadow.test.module.player.event;

import com.shadow.event.EventListener;
import com.shadow.test.module.player.entity.Player;
import com.shadow.util.codec.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/3/2
 */
@Component
public class PlayerLoginEventListener implements EventListener<PlayerLoginEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLoginEventListener.class);

    @Override
    public void onEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        LOGGER.info("Player login:{}", JsonUtil.toJson(player));
    }
}
