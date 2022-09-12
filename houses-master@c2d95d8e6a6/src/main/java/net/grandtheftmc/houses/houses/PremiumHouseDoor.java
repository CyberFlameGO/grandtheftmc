package net.grandtheftmc.houses.houses;

import org.bukkit.Location;

public class PremiumHouseDoor {

    private int hotspotId;

    private final int doorId;
    private final int houseId;
    private Location doorLocation;
    private Location insideLocation;
    private Location outsideLocation;

    public PremiumHouseDoor(int hotspotId, int doorId, int houseId) {
        this.hotspotId = hotspotId;
        this.doorId = doorId;
        this.houseId = houseId;
    }

    public PremiumHouseDoor(int hotspotId, int doorId, int houseId, Location doorLocation) {
        this.hotspotId = hotspotId;
        this.doorId = doorId;
        this.houseId = houseId;
        this.doorLocation = doorLocation;
    }

    public PremiumHouseDoor(int hotspotId, int doorId, int houseId, Location doorLocation, Location insideLocation, Location outsideLocation) {
        this.hotspotId = hotspotId;
        this.doorId = doorId;
        this.houseId = houseId;
        this.doorLocation = doorLocation;
        this.insideLocation = insideLocation;
        this.outsideLocation = outsideLocation;
    }

    public int getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(int hotspotId) {
        this.hotspotId = hotspotId;
    }

    public int getId() {
        return this.doorId;
    }

    public int getHouseId() {
        return this.houseId;
    }

    public Location getLocation() {
        return this.doorLocation;
    }

    public void setLocation(Location doorLocation) {
        this.doorLocation = doorLocation;
    }

    public Location getInsideLocation() {
        return this.insideLocation;
    }

    public void setInsideLocation(Location insideLocation) {
        this.insideLocation = insideLocation;
    }

    public Location getOutsideLocation() {
        return this.outsideLocation;
    }

    public void setOutsideLocation(Location outsideLocation) {
        this.outsideLocation = outsideLocation;
    }
}
