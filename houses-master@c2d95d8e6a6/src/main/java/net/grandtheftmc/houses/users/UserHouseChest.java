package net.grandtheftmc.houses.users;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.dao.HouseDAO;
import net.grandtheftmc.houses.houses.House;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class UserHouseChest {

    private final int houseId;
    private final int chestId;
    private ItemStack[] contents;

    public UserHouseChest(int houseId, int chestId) {
        this.houseId = houseId;
        this.chestId = chestId;
        this.contents = new ItemStack[]{};
    }

    public UserHouseChest(int houseId, int chestId, ItemStack[] contents) {
        this.houseId = houseId;
        this.chestId = chestId;
        this.contents = contents;
    }

    public int getId() {
        return this.chestId;
    }

    public int getHouseId() {
        return this.houseId;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public void setContents(ItemStack[] i) {
        this.contents = i;
    }

    public void updateContents(UUID uuid, int uniqueId) {
        ItemStack[] contents = this.contents;
        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                    try (PreparedStatement statement = connection.prepareStatement("update " + Core.name() + "_houses_chests set contents=? where uuid=? and houseId=? and chestId=?;")) {
//                        statement.setString(1, GTMUtils.toBase64(contents));
//                        statement.setString(2, uuid.toString());
//                        statement.setInt(3, UserHouseChest.this.houseId);
//                        statement.setInt(4, UserHouseChest.this.chestId);
//                        statement.execute();
//                    }
                    HouseDAO.updateChestContent(connection, uniqueId, chestId, uuid, contents);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

//                PreparedStatement ps = Core.sql.prepareStatement("update " + Core.name() + "_houses_chests set contents=? where uuid=? and houseId=? and chestId=?;");
//                try {
//                    ps.setString(1, GTMUtils.toBase64(contents));
//                    ps.setString(2, uuid.toString());
//                    ps.setInt(3, UserHouseChest.this.houseId);
//                    ps.setInt(4, UserHouseChest.this.chestId);
//                    ps.execute();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
            }
        }.runTaskAsynchronously(Houses.getInstance());

    }

}
