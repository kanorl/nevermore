package com.shadow.test.module.player.event;

import com.shadow.event.EventListener;
import com.shadow.test.module.player.entity.Player;
import com.shadow.util.codec.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author nevermore on 2015/3/4
 */
@Component
public class PlayerLogoutEventListener implements EventListener<PlayerLogoutEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLogoutEventListener.class);

    @Override
    public void onEvent(PlayerLogoutEvent event) {
        Player player = event.getPlayer();
        LOGGER.info("Player logout:{}", JsonUtil.toJson(player));
    }
}
