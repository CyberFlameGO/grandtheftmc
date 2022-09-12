package net.grandtheftmc.core.users.targets;

import org.bukkit.Location;

/**
 * Created by Timothy Lampen on 12/11/2017.
 */
public abstract class TrackedTarget {
    private char pointer;
    public abstract Location getLocation();

    public TrackedTarget(char pointer) {
        this.pointer = pointer;
    }

    public TrackedTarget() {
        this.pointer = '▲';
    }

    public char getPointer(){
        return this.pointer;
    }
}
