package net.grandtheftmc.core.events;

import net.grandtheftmc.core.editmode.WorldConfig;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerSwitchWorldEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;

    private final Location from;
    private final Location to;
    private final WorldConfig toWorldConfig;
    private boolean cancelled;

    public PlayerSwitchWorldEvent(Player player, Location from, Location to, WorldConfig toWorldConfig) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.toWorldConfig = toWorldConfig;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Location getFrom() {
        return this.from;
    }

    public Location getTo() {
        return this.to;
    }

    public WorldConfig getToWorldConfig() {
        return this.toWorldConfig;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
