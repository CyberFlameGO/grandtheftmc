package net.grandtheftmc.core.users.eventtag;

import net.grandtheftmc.core.events.CoreEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public final class PreTagEquipEvent extends CoreEvent implements Cancellable {

    private final Player player;
    private final EventTag from, to;

    private boolean cancel;

    /**
     * Construct a new Event
     */
    public PreTagEquipEvent(Player player, EventTag from, EventTag to) {
        super(false);
        this.player = player;
        this.from = from;
        this.to = to;
    }

    public Player getPlayer() {
        return player;
    }

    public EventTag getFrom() {
        return from;
    }

    public EventTag getTo() {
        return to;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancel = b;
    }
}
