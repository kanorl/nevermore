package com.shadow.test.module.session;

import com.shadow.event.EventBus;
import com.shadow.event.EventListener;
import com.shadow.socket.netty.server.session.SessionClosedEvent;
import com.shadow.test.module.player.event.PlayerLogoutEvent;
import com.shadow.test.module.player.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author nevermore on 2015/3/4
 */
@Component
public class SessionClosedEventListener implements EventListener<SessionClosedEvent> {

    @Autowired
    private EventBus eventBus;
    @Autowired
    private PlayerService playerService;

    @Override
    public void onEvent(SessionClosedEvent event) {
        Optional<Long> identity = event.getIdentity();
        if (!identity.isPresent()) {
            return;
        }
        playerService.getPlayer(identity.get()).ifPresent(p -> eventBus.post(PlayerLogoutEvent.valueOf(p)));
    }
}
