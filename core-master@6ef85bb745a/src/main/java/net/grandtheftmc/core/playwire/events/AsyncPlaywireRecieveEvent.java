package net.grandtheftmc.core.playwire.events;

import net.grandtheftmc.core.playwire.SocketMessageType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

/**
 * Created by Timothy Lampen on 2017-12-06.
 */
public class AsyncPlaywireRecieveEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final UUID uuid;
    private final SocketMessageType type;

    public AsyncPlaywireRecieveEvent(UUID uuid, SocketMessageType type) {
        this.uuid = uuid;
        this.type = type;
    }

    public SocketMessageType getType() {
        return type;
    }

    public UUID getUUID() {
        return uuid;
    }


    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }}
