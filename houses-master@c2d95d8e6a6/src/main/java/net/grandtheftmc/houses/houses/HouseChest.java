package net.grandtheftmc.houses.houses;

import org.bukkit.Location;

public class HouseChest {

    private int hotspotId;

    private final int chestId;
    private final int houseId;
    private Location loc1;
    private Location loc2;

    public HouseChest(int hotspotId, int chestId, int houseId, Location loc1) {
        this.hotspotId = hotspotId;
        this.chestId = chestId;
        this.houseId = houseId;
        this.loc1 = loc1;
    }

    public HouseChest(int hotspotId, int chestId, int houseId, Location loc1, Location loc2) {
        this.hotspotId = hotspotId;
        this.chestId = chestId;
        this.houseId = houseId;
        this.loc1 = loc1;
        this.loc2 = loc2;
    }

    public int getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(int hotspotId) {
        this.hotspotId = hotspotId;
    }

    public int getId() {
        return this.chestId;
    }

    public Location getLoc1() {
        return this.loc1;
    }

    public void setLoc1(Location l) {
        this.loc1 = l;
    }

    public Location getLoc2() {
        return this.loc2;
    }

    public void setLoc2(Location l) {
        this.loc2 = l;
    }

    public int getHouseId() {
        return this.houseId;
    }

}
