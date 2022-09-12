package com.j0ach1mmall3.wastedcops.api.events;

import com.j0ach1mmall3.wastedcops.api.Cop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class CopSpawnEvent extends CopEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private Player target;
    private Location location;

    public CopSpawnEvent(Cop cop, Player target, Location location) {
        super(cop);
        this.target = target;
        this.location = location;
    }

    public Player getTarget() {
        return this.target;
    }

    public void setTarget(Player newTarget) {
        if(newTarget == null || newTarget.getWorld() != location.getWorld()) return;
        this.target = newTarget;
    }

    public Location getLocation() {
        return new Location(this.location.getWorld(), this.location.getX(),
                this.location.getY(), this.location.getZ(), this.location.getYaw(),
                this.location.getPitch());
    }

    public void setLocation(Location newLocation) {
        if(newLocation == null || newLocation.getWorld() == null) return;
        this.location = newLocation;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
