package net.grandtheftmc.houses.houses;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.JSONHelper;
import net.grandtheftmc.houses.dao.HouseDAO;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.UserHouse;
import net.grandtheftmc.houses.users.UserHouseChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class House {

    private int uniqueIdentifier;

    private final int id;
    private int price;
    private List<HouseChest> chests = new ArrayList<>();
    private List<HouseDoor> doors = new ArrayList<>();
    private List<HouseSign> signs = new ArrayList<>();

    public House(int uniqueIdentifier, int id) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.id = id;
    }

    public House(int uniqueIdentifier, int id, int price) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.id = id;
        this.price = price;
    }

    public House(int uniqueIdentifier, int id, int price, List<HouseChest> chests, List<HouseDoor> doors, List<HouseSign> signs) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.id = id;
        this.price = price;
        this.chests = chests;
        this.doors = doors;
        this.signs = signs;
    }

    public int getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(int uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public int getId() {
        return this.id;
    }

    public List<HouseChest> getChests() {
        return this.chests;
    }

    public List<HouseChest> getChests(int start, int end) {
        return this.chests.stream().filter(door -> door.getId() >= start && door.getId() <= end).collect(Collectors.toList());
    }

    public int getAmountOfChests() {
        int i = this.chests.size();
        for (HouseChest chest : this.chests)
            if (chest.getLoc2() != null)
                i++;
        return i;
    }

    public HouseChest getChest(int id) {
        for (HouseChest chest : this.chests)
            if (chest.getId() == id)
                return chest;
        return null;
    }

    public HouseChest getChest(Location loc) {
        for (HouseChest chest : this.chests)
            if (chest.getLoc1().equals(loc) && chest.getLoc2().equals(loc))
                return chest;
        return null;
    }

    public List<HouseDoor> getDoors() {
        return this.doors;
    }

    public List<HouseDoor> getDoors(int start, int end) {
        return this.doors.stream().filter(door -> door.getId() >= start && door.getId() <= end).collect(Collectors.toList());
    }

    public HouseDoor getDoor(int id) {
        for (HouseDoor door : this.doors)
            if (door.getId() == id)
                return door;
        return null;
    }

    public HouseDoor getDoor() {
        if (this.doors.isEmpty())
            return null;
        for (int i = 1; ; i++) {
            HouseDoor door = this.getDoor(i);
            if (door != null)
                return door;
        }
    }

    public void removeAllDoors() {

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.removeAllDoors(connection, this.uniqueIdentifier);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.doors.clear();
        //TODO Remove all house doors from Database.
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int i) {
        //TODO Set database price.
        this.price = i;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.setPrice(connection, this.uniqueIdentifier, i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.updateSigns();
    }

    public int getUnusedChestId() {
        for (int i = 1; ; i++)
            if (this.getChest(i) == null)
                return i;
    }

    /**
     * Only use for data pulled from the Database.
     * @param chest
     */
    public void addChest(HouseChest chest) {
        this.chests.add(chest);
        int chestId = chest.getId();
        Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.getUserHouse(this.id).getChest(chestId));
        this.updateSigns();
    }

    public void addChest(int chestId, int houseId, Location first, Location second, Callback<HouseChest> callback) {
//        this.chests.add(chest);
//        int chestId = chest.getId();
//        Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.getUserHouse(this.id).getChest(chestId));
//        this.updateSigns();

        ServerUtil.runTaskAsync(() -> {
            HouseChest chest = new HouseChest(-1, chestId, houseId, first);
            if (second != null) chest.setLoc2(second);

            JSONHelper helper = new JSONHelper();
            helper.put("id", chestId);
            helper.put("loc1", HouseUtils.locationToString(first));
            if (second != null) helper.put("loc2", HouseUtils.locationToString(second));

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.addChest(connection, this.uniqueIdentifier, chest, helper);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            this.chests.add(chest);
            Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.getUserHouse(this.id).getChest(chestId));
            this.updateSigns();

            callback.call(chest);
        });
    }

    public void removeChest(HouseChest chest) {
        this.chests.remove(chest);

//        Core.sql.updateAsyncLater("delete from " + Core.name() + "_houses_chests where houseId=" + this.id + " and chestId="
//                + chest.getId() + ';');
//        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("delete from " + Core.name() + "_houses_chests where houseId=" + this.id + " and chestId=" + chest.getId() + ';'));
        //TODO Remove house chest.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteChestData(connection, chest.getHotspotId());
                HouseDAO.deleteUserChest(connection, this.uniqueIdentifier, chest.getId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.getUserHouse(this.id).removeChest(chest.getId()));
        this.updateSigns();
    }

    public void removeAllChestsNonDB() {
        this.chests.clear();
        Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.getUserHouse(this.id).removeChests());
        this.updateSigns();
    }

    public void removeAllChests() {
        this.chests.clear();

//        Core.sql.updateAsyncLater("delete from " + Core.name() + "_houses_chests where houseId=" + this.id + ';');
//        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("delete from " + Core.name() + "_houses_chests where houseId=" + this.id + ';'));
        //TODO Remove all house chests.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteAllChestData(connection, this.uniqueIdentifier);
                HouseDAO.deleteAllUserChest(connection, this.uniqueIdentifier);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.getUserHouse(this.id).removeChests());
        this.updateSigns();
    }

    public void removeAllOwnersNonDB() {
        Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.removeHouse(Bukkit.getPlayer(user.getUUID()), this));
        this.updateSigns();
    }

    public void removeAllOwners() {
//        Core.sql.updateAsyncLater("delete from " + Core.name() + "_houses where houseId=" + this.id + ';');
//        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("delete from " + Core.name() + "_houses where houseId=" + this.id + ';'));
        //TODO Remove all house owners.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteAllOwners(connection, this.uniqueIdentifier);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Houses.getUserManager().getLoadedUsers().stream().filter(user -> user.ownsHouse(this.id)).forEach(user -> user.removeHouse(Bukkit.getPlayer(user.getUUID()), this));
        this.updateSigns();
    }

    public void addDoor(Callback<HouseDoor> callback) {
        ServerUtil.runTaskAsync(() -> {
            HouseDoor door = new HouseDoor(-1, this.getUnusedDoorId(), this.id);

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.addDoor(connection, this.uniqueIdentifier, door, null);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            this.doors.add(door);
            callback.call(door);
        });

//        HouseDoor door = new HouseDoor(-1, this.getUnusedDoorId(), this.id);
//
//        this.doors.add(door);
//        return door;
    }

    public HouseDoor addDoor(HouseDoor door) {
        this.doors.add(door);
        return door;
    }

    private int getUnusedDoorId() {
        for (int i = 1; ; i++)
            if (this.getDoor(i) == null)
                return i;
    }

    public void removeDoor(HouseDoor door) {
        this.doors.remove(door);
        //TODO Remove door from Database.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.removeDoor(connection, this.uniqueIdentifier, door.getHotspotId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<HouseSign> getSigns() {
        return this.signs;
    }

    /**
     * Only used when pulling from database.
     * @param sign
     */
    public void addSign(HouseSign sign) {
        this.signs.add(sign);
        ServerUtil.runTask(() -> this.updateSign(sign.getLocation()));
//        this.updateSign(sign.getLocation());
    }

    public void addSign(Location loc, Callback<HouseSign> callback) {
        //TODO Add sign to database.
        ServerUtil.runTaskAsync(() -> {
            HouseSign sign = new HouseSign(-1, this.uniqueIdentifier, loc);

            JSONHelper helper = new JSONHelper();
            helper.put("loc", HouseUtils.locationToString(loc));

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.addSign(connection, this.uniqueIdentifier, sign, helper);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            this.signs.add(sign);

            ServerUtil.runTask(() -> this.updateSign(sign.getLocation()));
//            this.updateSign(sign.getLocation());
            callback.call(sign);
        });
    }

    public void removeSign(Location loc) {
        HouseSign sign = this.signs.stream().filter(s -> loc.equals(s.getLocation())).findFirst().orElse(null);
        if (sign == null) return;
        this.signs.remove(sign);
        //TODO Remove sign from Database.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteSign(connection, sign.getHotspotId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    public void removeAllSigns() {
        this.signs.clear();
        //TODO Remove all sign from Database.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteAllSigns(connection, this.uniqueIdentifier);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateSigns() {
        int id = this.id;
        new BukkitRunnable() {
            @Override
            public void run() {
                House house = Houses.getHousesManager().getHouse(id);
                if (house != null)
                    new ArrayList<>(house.getSigns()).forEach(s -> updateSign(s.getLocation()));
            }
        }.runTask(Houses.getInstance());
    }

    public void updateSign(Location loc) {
        if (loc == null) return;

        BlockState state = loc.getBlock().getState();
        if (!(state instanceof Sign)) {
            this.removeSign(loc);
            return;
        }

        Sign sign = (Sign) state;
        sign.setLine(0, Utils.f("&3&lHouse"));
        sign.setLine(1, Utils.f("&8Chests: &a&l" + this.getAmountOfChests()));
        sign.setLine(2, Utils.f("&8Price:"));
        sign.setLine(3, Utils.f("&a$&l" + this.price));
        sign.update();
    }

    public void openChest(Player player, Location chestLoc, HouseUser user, HouseChest chest) {
        if (!user.ownsHouse(this.id)) {
            player.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this house!"));
            return;
        }

        if(player.getOpenInventory().getType() == InventoryType.CHEST) return;
        boolean isDub = chest.getLoc2() != null;
        UserHouse userHouse = user.getUserHouse(this.id);
        UserHouseChest userChest = userHouse.getChest(chest.getId());
        Inventory inv = Bukkit.createInventory(null, isDub ? 54 : 27, Utils.f("&3&lChest: &a&l" + this.id + ',' + chest.getId()));
        inv.setContents(userChest.getContents());

        if (chestLoc != null)
            Utils.playChestAnimation(player, chestLoc, true);

        player.openInventory(inv);
    }

    public boolean buyHouse(Player player, User user, GTMUser gtmUser, HouseUser houseUser) {
        if (houseUser.ownsHouse(this.id)) {
            player.sendMessage(Utils.f(Lang.HOUSES + "&7You already own this house!"));
            return true;
        }

        if (houseUser.hasMaxHouses(player, user, gtmUser)) {
            player.sendMessage(Lang.HOUSES.f("&7You have hit the maximum amount of houses you can own!"));
            return false;
        }

        if (!gtmUser.hasMoney(this.price)) {
            player.sendMessage(Lang.HOUSES.f("&7You can not afford the &a$&l" + this.price + "&7 to pay for this house!"));
            return true;
        }

        if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return true;
        }

        gtmUser.takeMoney(this.price);
        GTMUtils.updateBoard(player, gtmUser);
        houseUser.addHouse(this);//TODO Make sure this method updates database.
        Utils.insertLogLater(player.getUniqueId(), player.getName(), "buyHouseMethod", "BUY_HOUSE", "House ID: " + this.id,1,this.price);
        player.sendMessage(Lang.HOUSES.f("&7You bought house &a" + this.id + "&7 for &a$&l" + this.price + "&7!"));
        return true;
    }

    public void addOwner(Player player, HouseUser user) {

        if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return;
        }

        if (user.getUserHouse(this.id) == null) {
            player.sendMessage(Lang.HOUSES.f("&7You now own house &a" + this.id + "&7!"));
            user.addHouse(this);//TODO Make sure this method adds a user house.
        } else
            player.sendMessage(Lang.HOUSES.f("&7You already own house &a" + this.id + "&7!"));
    }

    public boolean sellHouse(Player player, GTMUser gtmUser, HouseUser user) {
        if (!user.ownsHouse(this.id)) {
            player.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this house!"));
            return true;
        }

        if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return true;
        }

        gtmUser.addMoney(this.price / 2);
        GTMUtils.updateBoard(player, gtmUser);
        user.removeHouse(player, this);//TODO Make sure this method removes all data.
        Utils.insertLogLater(player.getUniqueId(), player.getName(), "sellHouseMethod", "SELL_HOUSE", "House ID: " + this.id,1,this.price/2);
        player.sendMessage(Lang.HOUSES.f("&7You sold house &a" + this.id + "&7 for &a$&l" + (this.price / 2) + "&7!"));
        return true;
    }

    public void removeOwner(Player player, HouseUser user) {

        if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return;
        }

        if (user.ownsHouse(this.id)) {
            player.sendMessage(Lang.HOUSES.f("&7You no longer own house &a" + this.id + "&7!"));
            user.removeHouse(player, this);//TODO Make sure this method removes all data.
            if (user.isInsideHouse(this.id))
                user.teleportInOrOutHouse(player, this);
        } else
            player.sendMessage(Lang.HOUSES.f("&7You don't own house &a" + this.id + "&7!"));
    }


}
