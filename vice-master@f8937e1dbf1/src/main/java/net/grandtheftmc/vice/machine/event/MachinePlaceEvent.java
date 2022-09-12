package net.grandtheftmc.vice.machine.event;

import net.grandtheftmc.core.events.CoreEvent;
import net.grandtheftmc.vice.machine.BaseMachine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public final class MachinePlaceEvent extends CoreEvent implements Cancellable {

    private final Player player;
    private final Location location;
    private final BaseMachine machine;

    private boolean cancelled;

    public MachinePlaceEvent(Player player, Location location, BaseMachine machine) {
        super(false);
        this.player = player;
        this.location = location;
        this.machine = machine;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public BaseMachine getMachine() {
        return machine;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }
}
