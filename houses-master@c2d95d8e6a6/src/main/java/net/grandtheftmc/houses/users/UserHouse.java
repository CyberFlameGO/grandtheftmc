package net.grandtheftmc.houses.users;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.dao.HouseDAO;
import net.grandtheftmc.houses.houses.House;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserHouse {

    private int uniqueId;
    private final UUID uuid;
    private final int houseId;
    private List<UserHouseChest> chests = new ArrayList<>();

    public UserHouse(UUID uuid, int uniqueId, int houseId) {
        this.uuid = uuid;
        this.uniqueId = uniqueId;
        this.houseId = houseId;
    }

    public UserHouse(UUID uuid, int uniqueId, int houseId, List<UserHouseChest> chests) {
        this.uuid = uuid;
        this.uniqueId = uniqueId;
        this.houseId = houseId;
        this.chests = chests;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getId() {
        return this.houseId;
    }

    public UserHouseChest getChestOrNull(int id) {
        for (UserHouseChest chest : this.chests)
            if (chest.getId() == id)
                return chest;
        return null;
    }

    public UserHouseChest getChest(int id) {
        for (UserHouseChest chest : this.chests)
            if (chest.getId() == id)
                return chest;

        House house = Houses.getHousesManager().getHouse(this.houseId);
        if (house.getChest(id) != null) {
            UserHouseChest chest = new UserHouseChest(this.houseId, id);

//            Core.sql.updateAsyncLater("insert into " + Core.name() + "_houses_chests(uuid,houseId,chestId) values ('" + this.uuid + "'," + this.houseId + ',' + id + ");");
            ServerUtil.runTaskAsync(() -> {
//                BaseDatabase.runCustomQuery("insert into " + Core.name() + "_houses_chests(uuid,houseId,chestId) values ('" + this.uuid + "'," + this.houseId + ',' + id + ");");
                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                    HouseDAO.addChestContent(connection, house.getUniqueIdentifier(), id, this.uuid, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            this.chests.add(chest);
            return chest;
        }
        return null;
    }

    public void addChest(UserHouseChest chest) {
        UserHouseChest old = this.getChestOrNull(chest.getId());
        if (old == null) this.chests.add(chest);
        else old.setContents(chest.getContents());
    }

    public void removeChest(int id) {
        this.chests.remove(this.getChest(id));
    }

    public void removeChests() {
        this.chests.clear();
    }

    public List<UserHouseChest> getChests() {
        return chests;
    }
}
