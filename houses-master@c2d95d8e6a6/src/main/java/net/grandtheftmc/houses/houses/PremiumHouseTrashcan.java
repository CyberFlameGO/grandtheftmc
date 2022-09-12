package net.grandtheftmc.houses.houses;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.JSONHelper;
import net.grandtheftmc.houses.dao.PremiumHouseDAO;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.SQLException;

public class PremiumHouseTrashcan {

    private int hotspotId;

    private final int trashcanId;
    private final int houseId;
    private boolean owned = false;
    private Location location;

    public PremiumHouseTrashcan(int hotspotId, int trashcanId, int houseId, Location location, boolean owned) {
        this.hotspotId = hotspotId;
        this.trashcanId = trashcanId;
        this.houseId = houseId;
        this.location = location;
        this.owned = owned;
    }

    public int getHotspotId() {
        return hotspotId;
    }

    public void setHotspotId(int hotspotId) {
        this.hotspotId = hotspotId;
    }

    public int getId() {
        return this.trashcanId;
    }

    public int getHouseId() {
        return this.houseId;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isOwned() {
        return this.owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.setTrashcanOwned(connection, this.hotspotId, new JSONHelper().put("id", this.trashcanId).put("loc", HouseUtils.locationToString(this.location)).put("owned", owned));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
