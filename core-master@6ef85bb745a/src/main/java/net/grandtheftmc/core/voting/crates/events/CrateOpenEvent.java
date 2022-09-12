package net.grandtheftmc.core.voting.crates.events;

import net.grandtheftmc.core.voting.Reward;
import net.grandtheftmc.core.voting.crates.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Timothy Lampen on 2017-04-28.
 */
public class CrateOpenEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Crate crate;

    public CrateOpenEvent(Player player, Crate crate) {
        super(player);
        this.crate = crate;
    }

    public Crate getCrate() {
        return crate;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
