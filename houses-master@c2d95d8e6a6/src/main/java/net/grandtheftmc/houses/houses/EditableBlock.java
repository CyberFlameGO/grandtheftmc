package net.grandtheftmc.houses.houses;

import org.bukkit.Location;
import org.bukkit.Material;

public class EditableBlock {

    private int hotspotId;

    private Location location;
    private Material defaultType;
    private byte defaultData;

    public EditableBlock(int hotspotId, Location location, Material defaultType, byte defaultData) {
        this.hotspotId = hotspotId;
        this.location = location;
        this.defaultType = defaultType;
        this.defaultData = defaultData;
    }

    public int getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(int hotspotId) {
        this.hotspotId = hotspotId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(Material defaultType) {
        this.defaultType = defaultType;
    }

    public byte getDefaultData() {
        return defaultData;
    }

    public void setDefaultData(byte defaultData) {
        this.defaultData = defaultData;
    }
}
