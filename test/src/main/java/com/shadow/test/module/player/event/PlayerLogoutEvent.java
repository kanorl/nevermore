package com.shadow.test.module.player.event;

import com.shadow.event.Event;
import com.shadow.test.module.player.entity.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author nevermore on 2015/3/2
 */
public class PlayerLogoutEvent implements Event {
    private final Player player;

    public PlayerLogoutEvent(@Nonnull Player player) {
        this.player = Objects.requireNonNull(player);
    }

    public static PlayerLogoutEvent valueOf(Player player) {
        return new PlayerLogoutEvent(player);
    }

    @Nonnull
    public Player getPlayer() {
        return player;
    }
}
