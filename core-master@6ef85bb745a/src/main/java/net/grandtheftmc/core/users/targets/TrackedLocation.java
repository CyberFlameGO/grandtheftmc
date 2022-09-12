package net.grandtheftmc.core.users.targets;

import org.bukkit.Location;

/**
 * Created by Timothy Lampen on 12/11/2017.
 */
public class TrackedLocation extends TrackedTarget {

    private final Location loc;
    public TrackedLocation (Location loc, char pointer){
        super(pointer);
        this.loc = loc;
    }

    public TrackedLocation (Location loc){
        this.loc = loc;
    }

    @Override
    public Location getLocation() {
        return this.loc;
    }
}
