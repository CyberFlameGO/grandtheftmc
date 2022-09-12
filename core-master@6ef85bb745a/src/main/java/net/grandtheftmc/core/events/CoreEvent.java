package net.grandtheftmc.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Luke Bingham on 06/07/2017.
 */
public class CoreEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Construct a new Event
     */
    public CoreEvent(boolean async) {
        super(async);
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
