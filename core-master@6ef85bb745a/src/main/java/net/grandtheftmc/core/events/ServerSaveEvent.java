package net.grandtheftmc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerSaveEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public ServerSaveEvent() {

    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
