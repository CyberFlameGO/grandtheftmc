package net.grandtheftmc.core.voting.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerVoteEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final UUID uuid;
    private final String timestamp;
    private final String address;
    private final String serviceName;
    private boolean cancelled;

    public PlayerVoteEvent(UUID uuid, String timestamp, String address, String serviceName) {
        this.uuid = uuid;
        this.timestamp = timestamp;
        this.address = address;
        this.serviceName = serviceName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAddress() {
        return address;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
