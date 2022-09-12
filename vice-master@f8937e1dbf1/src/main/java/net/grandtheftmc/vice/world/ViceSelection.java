package net.grandtheftmc.vice.world;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Timothy Lampen on 8/25/2017.
 */
public class ViceSelection {

    private final Location lowCorner, highCorner;
    private final List<ZoneFlag> flags;
    private final String name;
    private final List<UUID> insideSelection = new ArrayList<>();

    public ViceSelection(String name, List<ZoneFlag> flags, Location corner1, Location corner2, boolean ignoreheight) {
        if(corner1.getBlockY()==corner2.getBlockY() || ignoreheight) {
            corner1.setY(0);
            corner2.setY(255);
            this.lowCorner = corner1;
            this.highCorner = corner2;
        }
        else {
            this.lowCorner = corner1.getBlockY()<corner2.getBlockY() ? corner1 : corner2;
            this.highCorner = corner1.getBlockY()>corner2.getBlockY() ? corner1 : corner2;
        }
        this.name = name;
        this.flags = flags;
    }

    public String getName() {
        return name;
    }

    public List<ZoneFlag> getFlags() {
        return flags;
    }

    public void addFlag(ZoneFlag flag) {
        this.flags.add(flag);
    }

    public boolean removeFlag(ZoneFlag flag){
        if(this.flags.contains(flag)) {
            this.flags.remove(flag);
            return true;
        }
        return false;
    }

    public Location getHighCorner() {
        return highCorner;
    }

    public Location getLowCorner() {
        return lowCorner;
    }

    public boolean isInRegion(Location loc){
        if(loc.getY()>=this.lowCorner.getY() && loc.getY()<=this.highCorner.getY()) {
            double[] dim = new double[2];

            dim[0] = this.highCorner.getX();
            dim[1] = this.lowCorner.getX();
            Arrays.sort(dim);
            if(loc.getX() > dim[1] || loc.getX() < dim[0])
                return false;

            dim[0] = this.highCorner.getZ();
            dim[1] = this.lowCorner.getZ();
            Arrays.sort(dim);
            if(loc.getZ() > dim[1] || loc.getZ() < dim[0])
                return false;
            return true;
        }
        return false;
    }

    public List<UUID> getInsideSelection() {
        return insideSelection;
    }

    public void addPlayerToSelection(UUID uuid) {
        if(!this.insideSelection.contains(uuid))
            this.insideSelection.add(uuid);
    }

    public void removePlayerFromSelection(UUID uuid) {
        if(this.insideSelection.contains(uuid))
            this.insideSelection.remove(uuid);
    }
}
