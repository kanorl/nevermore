package com.shadow.test.module.player.event;

import com.shadow.common.util.codec.JsonUtil;
import com.shadow.event.EventListener;
import com.shadow.socket.core.domain.Command;
import com.shadow.socket.core.session.SessionManager;
import com.shadow.test.module.player.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author nevermore on 2015/3/2
 */
@Component
public class PlayerLoginEventListener implements EventListener<PlayerLoginEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLoginEventListener.class);

    @Autowired
    private SessionManager sessionManager;

    @Override
    public void onEvent(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        LOGGER.info("Player login:{}", JsonUtil.toJson(player));

        sessionManager.send(player.getId(), Command.valueOf((short) 1, (byte) 3), "Welcome " + player.getName() + " to login");

        sessionManager.sendOnly(Command.valueOf((short) 1, (byte) 4), "Send Only You", e -> Objects.equals(e, player.getId()));
    }
}
