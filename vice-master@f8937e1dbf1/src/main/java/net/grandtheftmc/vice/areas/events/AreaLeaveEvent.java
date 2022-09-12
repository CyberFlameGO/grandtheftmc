package net.grandtheftmc.vice.areas.events;

import net.grandtheftmc.vice.areas.obj.Area;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AreaLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Area area;

    public AreaLeaveEvent(final Player player, final Area area) {
        this.player = player;
        this.area = area;
    }

    public static final HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public final HandlerList getHandlers() {
        return handlers;
    }

    public final Player getPlayer() {
        return this.player;
    }

    public final Area getArea() {
        return this.area;
    }

}
