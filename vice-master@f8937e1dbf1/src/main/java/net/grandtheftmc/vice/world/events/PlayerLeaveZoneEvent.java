package net.grandtheftmc.vice.world.events;

import net.grandtheftmc.vice.world.ViceSelection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * Created by Timothy Lampen on 8/25/2017.
 */
public class PlayerLeaveZoneEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled;
    private final ViceSelection zone;

    public PlayerLeaveZoneEvent(Player who, ViceSelection zone) {
        super(who);
        this.zone = zone;
    }

    public ViceSelection getZone() {
        return zone;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}