package net.grandtheftmc.houses.houses;

import org.bukkit.Location;

public class HouseSign {

    private int hotspotId;

    private final int houseId;
    private Location location;

    public HouseSign(int hotspotId, int houseId, Location location) {
        this.hotspotId = hotspotId;
        this.houseId = houseId;
        this.location = location;
    }

    public int getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(int hotspotId) {
        this.hotspotId = hotspotId;
    }

    public int getHouseId() {
        return houseId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
