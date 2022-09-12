package net.grandtheftmc.core.data;

import org.bukkit.Location;

/**
 * Created by Adam on 13/06/2017.
 */
public class CompactLoc {

    /*
        Use this class as a quick and lightweight way of comparing player locations.
     */

    private final int x, z;
    //If player has moved at least 1 block we mark them as not being afk.
    private final static int threshold = 1;

    public CompactLoc(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public CompactLoc(Location l) {
        this.x = l.getBlockX();
        this.z = l.getBlockZ();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    /**
     * Check whether a CompactLoc is different from another within a configured range.
     * @param loc The location to compare with.
     * @return True if the distance between locations > threshold.
     */
    public boolean differs(CompactLoc loc) {
        //Keep it quick and simple.
        int diff = Math.abs(loc.getX() - x) + Math.abs(loc.getZ() - z);
        return diff >= threshold;
    }
}
