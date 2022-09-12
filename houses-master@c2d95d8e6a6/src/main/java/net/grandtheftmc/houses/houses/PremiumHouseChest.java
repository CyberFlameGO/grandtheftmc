package net.grandtheftmc.houses.houses;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;

public class PremiumHouseChest {

    private int hotspotId;

    private final int chestId;
    private final int houseId;
    private Location loc1;
    private Location loc2;

    public PremiumHouseChest(int hotspotId, int chestId, int houseId, Location loc1) {
        this.hotspotId = hotspotId;
        this.chestId = chestId;
        this.houseId = houseId;
        this.loc1 = loc1;
    }

    public PremiumHouseChest(int hotspotId, int chestId, int houseId, Location loc1, Location loc2) {
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

    public int getHouseId() {
        return this.houseId;
    }

    public Location getLoc1() {
        return this.loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public Location getLoc2() {
        return this.loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
    }

    public void clear() {
        BlockState state = this.getLoc1().getBlock().getState();
        if (state.getType() != Material.CHEST) return;
        Chest chest = (Chest) state;
        chest.getBlockInventory().clear();
        if (this.getLoc2() != null) {
            state = this.getLoc2().getBlock().getState();
            if (state.getType() != Material.CHEST) return;
            chest = (Chest) state;
            chest.getBlockInventory().clear();
        }
    }

}
