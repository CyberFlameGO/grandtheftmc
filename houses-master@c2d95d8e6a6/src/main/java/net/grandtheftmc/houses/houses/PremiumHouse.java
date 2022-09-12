package net.grandtheftmc.houses.houses;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.JSONHelper;
import net.grandtheftmc.houses.dao.HouseDAO;
import net.grandtheftmc.houses.dao.PremiumHouseDAO;
import net.grandtheftmc.houses.users.HouseUser;

public class PremiumHouse {

    private int uniqueIdentifier;

    private final int id;
    private int permits;
    private UUID owner;
    private String ownerName;
    private List<PremiumHouseChest> chests = new ArrayList<>();
    private List<PremiumHouseDoor> doors = new ArrayList<>();
    private List<PremiumHouseGuest> guests = new ArrayList<>();
    private List<HouseSign> signs = new ArrayList<>();
    private Collection<EditableBlock> editableBlocks = new ArrayList<>();
    private Collection<PremiumHouseTrashcan> trashcans = new ArrayList<>();

    public PremiumHouse(int uniqueIdentifier, int id) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.id = id;
    }

    public PremiumHouse(int uniqueIdentifier, int id, int permits) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.id = id;
        this.permits = permits;
    }

    public PremiumHouse(int uniqueIdentifier, int id, int permits, UUID owner, String ownerName, List<PremiumHouseDoor> doors,
                        List<PremiumHouseChest> chests, List<HouseSign> signs, List<PremiumHouseGuest> guests,
                        Collection<EditableBlock> editableBlocks, Collection<PremiumHouseTrashcan> trashcans) {
        this.uniqueIdentifier = uniqueIdentifier;
        this.id = id;
        this.permits = permits;
        this.owner = owner;
        this.ownerName = ownerName;
        this.doors = doors;
        this.chests = chests;
        this.signs = signs;
        this.guests = guests;
        this.editableBlocks = editableBlocks;
        this.trashcans = trashcans;
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

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(Player player) {
        if (this.owner != null) {
            if (Objects.equals(this.owner, player.getUniqueId())) {
                player.sendMessage(Lang.HOUSES.f("&7You already own premium house &a" + this.id + "&7!"));
                return;
            }
            player.sendMessage(Utils.f(Lang.HOUSES + "&7Someone already owns premium house &a" + this.id + "&7!"));
            return;
        }
        player.sendMessage(Lang.HOUSES.f("&7You now own premium house &a" + this.id + "&7!"));
        this.setOwner(player.getUniqueId(), player.getName(), true);
    }

    public boolean isOwner(UUID uuid) {
        return Objects.equals(uuid, this.owner);
    }

    public int getPermits() {
        return this.permits;
    }

    public void setPermits(int i) {
        this.permits = i;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.setPrice(connection, this.uniqueIdentifier, i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.updateSigns();
    }

    public List<PremiumHouseChest> getChests() {
        return this.chests;
    }

    public List<PremiumHouseChest> getChests(int start, int end) {
        return this.chests.stream().filter(chest -> chest.getId() >= start && chest.getId() <= end).collect(Collectors.toList());
    }

    public int getAmountOfChests() {
        int i = this.chests.size();
        i += this.chests.stream().filter(chest -> chest.getLoc2() != null).count();
        return i;
    }

    public PremiumHouseChest getChest(int id) {
        return this.chests.stream().filter(chest -> chest.getId() == id).findFirst().orElse(null);
    }

    public List<PremiumHouseGuest> getGuests() {
        return this.guests;
    }

    public PremiumHouseGuest getGuest(UUID uuid) {
        return this.guests.stream().filter(guest -> Objects.equals(guest.getUuid(), uuid)).findFirst().orElse(null);
    }

    public PremiumHouseGuest getGuest(String name) {
        return this.guests.stream().filter(guest -> Objects.equals(guest.getName(), name)).findFirst().orElse(null);
    }

    public void addGuest(UUID uuid, String name) {
        if (this.getGuest(uuid) == null) {
            this.guests.add(new PremiumHouseGuest(uuid, name));
        }
    }

    /**
     * Only use when pulling from database.
     * @param guest
     */
    public void addGuest(PremiumHouseGuest guest) {
        this.guests.add(guest);
    }

    public boolean isGuest(UUID uuid) {
        return this.getGuest(uuid) != null;
    }

    public boolean isGuest(String guest) {
        return this.getGuest(guest) != null;
    }

    public boolean hasAccess(Player player, HouseUser user) {
        return player.isOp() || this.isGuest(player.getUniqueId()) || this.isOwner(player.getUniqueId());
    }

    public List<PremiumHouseDoor> getDoors() {
        return this.doors;
    }

    public List<PremiumHouseDoor> getDoors(int start, int end) {
        return this.doors.stream().filter(door -> door.getId() >= start && door.getId() <= end).collect(Collectors.toList());
    }

    public PremiumHouseDoor getDoor(int id) {
        return this.doors.stream().filter(door -> door.getId() == id).findFirst().orElse(null);
    }

    public PremiumHouseDoor getDoor() {
        if (this.doors.isEmpty())
            return null;
        for (int i = 1; ; i++) {
            PremiumHouseDoor door = this.getDoor(i);
            if (door != null)
                return door;
        }
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
        this.updateSigns();
    }

    public boolean isOwned() {
        return this.owner != null;
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
    public PremiumHouseChest addChest(PremiumHouseChest chest) {
        this.chests.add(chest);
        this.updateSigns();
        return chest;
    }

    public void addChest(int chestId, int houseId, Location first, Location second, Callback<PremiumHouseChest> callback) {
//        this.chests.add(chest);
//        this.updateSigns();
//        return chest;

        ServerUtil.runTaskAsync(() -> {
            PremiumHouseChest chest = new PremiumHouseChest(-1, chestId, houseId, first);
            if (second != null) chest.setLoc2(second);

            JSONHelper helper = new JSONHelper();
            helper.put("id", chestId);
            helper.put("loc1", HouseUtils.locationToString(first));
            if (second != null) helper.put("loc2", HouseUtils.locationToString(second));

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.addChest(connection, this.uniqueIdentifier, chest, helper);
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

    public void removeChest(PremiumHouseChest chest) {
        this.chests.remove(chest);

        //TODO Remove house chest.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteChestData(connection, chest.getHotspotId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.updateSigns();
    }

    public void removeAllChests() {
        this.chests.clear();

        //TODO Remove all house chests.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteAllChestData(connection, this.uniqueIdentifier);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.updateSigns();
    }

    public void addDoor(Callback<PremiumHouseDoor> callback) {
        ServerUtil.runTaskAsync(() -> {
            PremiumHouseDoor door = new PremiumHouseDoor(-1, this.getUnusedDoorId(), this.id);

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.addDoor(connection, this.uniqueIdentifier, door, null);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            this.doors.add(door);
            callback.call(door);
        });
    }

    public PremiumHouseDoor addDoor(PremiumHouseDoor door) {
        this.doors.add(door);
        return door;
    }

    private int getUnusedDoorId() {
        for (int i = 1; ; i++)
            if (this.getDoor(i) == null)
                return i;
    }

    public void removeDoor(PremiumHouseDoor door) {
        this.doors.remove(door);
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.removeDoor(connection, this.uniqueIdentifier, door.getHotspotId());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeAllDoors() {
        this.doors.clear();

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.removeAllDoors(connection, this.uniqueIdentifier);
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
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteAllSigns(connection, this.uniqueIdentifier);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<HouseSign> getSigns(int start, int end) {
        return this.signs.subList(start, end);
    }

    public void updateSigns() {
        int id = this.id;
        new BukkitRunnable() {
            @Override
            public void run() {
                PremiumHouse house = Houses.getHousesManager().getPremiumHouse(id);
                if (house != null)
                    new ArrayList<>(house.getSigns()).forEach(s -> updateSign(s.getLocation()));
            }
        }.runTask(Houses.getInstance());

    }

    public void updateSign(Location loc) {
        BlockState state = loc.getBlock().getState();
        if (!(state instanceof Sign)) {
            this.removeSign(loc);
            return;
        }
        Sign sign = (Sign) state;
        sign.setLine(0, Utils.f("&3&lPremium House &a&l" + this.id));
        sign.setLine(1, Utils.f("&8Chests: &a&l" + this.getAmountOfChests()));
        sign.setLine(2, Utils.f("&8Permits: &a&l" + this.permits));
        sign.setLine(3, Utils.f(this.isOwned() ? "&cOwned" : "&aVacant"));
        sign.update();
    }

    public void buy(Player player, User user, GTMUser gtmUser, HouseUser houseUser) {
        if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return;
        }

        if (this.owner != null) {
            if (Objects.equals(this.owner, player.getUniqueId())) {
                player.sendMessage(Utils.f(Lang.HOUSES + "&7You already own this premium house!"));
                return;
            }
            player.sendMessage(Utils.f(Lang.HOUSES + "&7This premium house is already owned by &a" + this.owner + "&7!"));
            return;
        }

        /**
         * Prevent house from being bought.
         *
         * Ignore the silly if check, it's so i can find the if statement later.
         */
//        if ("disable".equalsIgnoreCase("disable")) {
//            player.sendMessage(Utils.f(Lang.HOUSES + "&7Purchasing premium houses is temporarily disabled."));
//            player.sendMessage(Utils.f(Lang.HOUSES + "&7Try again later.."));
//            return;
//        }

        if (houseUser.hasMaxHouses(player, user, gtmUser)) {
            player.sendMessage(Lang.HOUSES.f("&7You have hit the maximum amount of houses you can own!"));
            return;
        }
        if (!gtmUser.hasPermits(this.permits)) {
            player.sendMessage(Lang.HOUSES.f("&7You can not afford the &3&l" + this.permits + " Permits&7 to pay for this premium house!"));
            return;
        }
        gtmUser.takePermits(this.permits);
        this.setOwner(player.getUniqueId(), player.getName(), true);//TODO Make sure this method updates database.
        player.sendMessage(Lang.HOUSES.f("&7You bought premium house &a" + this.id + "&7 for &3&l" + this.permits + " Permits&7!"));
        Utils.insertLogLater(player.getUniqueId(), player.getName(), "buyPremiumHouseMethod", "BUY_PREMIUM_HOUSE", "Premium House ID: " + this.id,1,this.permits);
        Bukkit.broadcastMessage(Lang.HOUSES.f(user.getColoredName(player) + " &7bought premium house " + "&a" + this.id + " &7for &3&l" + this.permits + " Permits&7!"));
        Houses.getHousesManager().save();
    }

    public void forceSetOwner(Player player) {
    	if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return;
        }

        Player own = Bukkit.getPlayer(this.owner);
        if (own != null) {
            HouseUser user = Houses.getUserManager().getLoadedUser(own.getUniqueId());
            own.sendMessage(Lang.HOUSES.f("&7You no longer own premium house &a" + this.id + "&7!"));
            if (user.isInsidePremiumHouse(this.id))
                user.teleportInOrOutPremiumHouse(own, this);
        }
        player.sendMessage(Lang.HOUSES.f("&7You now own premium house &a" + this.id + "&7!"));
        this.setOwner(player.getUniqueId(), player.getName(), true);//TODO Make sure this method updates database.
    }

    public void setOwner(UUID uuid, String name, boolean clearGuests) {
    	this.owner = uuid;
        this.ownerName = name;
        if (clearGuests)
            this.guests.clear();


        //TODO Set owner of premium house.
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                
            	// if we clear guests
            	if (clearGuests){
            		// delete all guests
            		PremiumHouseDAO.deleteHouseUsers(connection, this.uniqueIdentifier, false);
            	}
            	
            	// add the owner as the owner of the house
                PremiumHouseDAO.addHouseGuest(connection, this.uniqueIdentifier, uuid, true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            ServerUtil.runTask(this::updateSigns);
        });
    }

    /**
     * Only use for setting data via DAO's
     */
    public void setOwner(UUID uuid, String name) {
        this.owner = uuid;
        this.ownerName = name;

        this.updateSigns();
    }

    public void sell(Player player, GTMUser gtmUser, HouseUser houseUser) {
        if (this.owner == null) {
            player.sendMessage(Utils.f(Lang.HOUSES + "&7No one owns this premium house!"));
            return;
        }

        if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return;
        }

        if (!Objects.equals(this.owner, player.getUniqueId())) {
            player.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this premium house!"));
            return;
        }

        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        gtmUser.addPermits(this.permits);
        this.removeOwner(true);//TODO Make sure this method updates database.
        if (houseUser.isInsidePremiumHouse(this.id))
            houseUser.teleportInOrOutPremiumHouse(player, this);
        this.getEditableBlocks().forEach(block -> {
            block.getLocation().getBlock().setType(block.getDefaultType());
            block.getLocation().getBlock().setData(block.getDefaultData());
        });
        this.getTrashcans().stream().filter(PremiumHouseTrashcan::isOwned).forEach(trashCan -> {
            trashCan.setOwned(false);//TODO Make sure this method updates database.
            gtmUser.addPermits(5);
        });
        this.getChests().forEach(PremiumHouseChest::clear);
        Utils.insertLogLater(player.getUniqueId(), player.getName(), "sellPremiumHouseMethod", "SELL_PREMIUM_HOUSE", "Premium House ID: " + this.id,1,this.permits);
        Bukkit.broadcastMessage(Lang.HOUSES.f(user.getColoredName(player) + " &7sold premium house " + "&a" + this.id + " &7for &3&l" + this.permits + " Permits&7!"));
        Houses.getHousesManager().save();
    }

    public void removeOwner(Player player, HouseUser user) {
        if (!Objects.equals(this.owner, player.getUniqueId())) {
            player.sendMessage(Lang.HOUSES.f("&7You don't own premium house &a" + this.id + "&7!"));
            return;
        }

        if (!Houses.ENABLED) {
            player.sendMessage(Lang.HOUSES.f("&cHouses is currently disabled, try again soon!"));
            return;
        }

        player.sendMessage(Lang.HOUSES.f("&7You no longer own premium house &a" + this.id + "&7!"));
        this.removeOwner(true);//TODO Make sure this method updates database.
        if (user.isInsidePremiumHouse(this.id))
            user.teleportInOrOutPremiumHouse(player, this);
    }

    public void removeOwner(boolean clearGuests) {
        this.owner = null;
        this.ownerName = null;
        if (clearGuests)
            this.guests.clear();

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.deleteHouseUsers(connection, this.uniqueIdentifier, true);
                if (clearGuests) PremiumHouseDAO.deleteHouseUsers(connection, this.uniqueIdentifier, false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.updateSigns();
    }

    public void addGuest(Player sender, Player target, HouseUser user) {
        if (!Objects.equals(this.owner, sender.getUniqueId())) {
            sender.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this premium house!"));
            return;
        }

        if (Objects.equals(sender, target)) {
            sender.sendMessage(Utils.f(Lang.HOUSES + "&7You can't add yourself as a guest!"));
            return;
        }

        if (this.isGuest(target.getUniqueId())) {
            sender.sendMessage(Utils.f(Lang.HOUSES + "&7That player is already a guest of this premium house!"));
            return;
        }

        sender.sendMessage(Lang.HOUSES.f("&7You added &a" + target.getName() + "&7 as a guest to premium house &a" + this.id + '!'));
        target.sendMessage(Lang.HOUSES.f("&a" + sender.getName() + "&7 added you a guest to premium house &a" + this.id + "&7!"));
        this.addGuest(target);//TODO Make sure this method updates database.
    }

    public void addGuest(Player player) {
        this.guests.add(new PremiumHouseGuest(player.getUniqueId(), player.getName()));

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.addHouseGuest(connection, this.uniqueIdentifier, player.getUniqueId(), false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeGuest(Player sender, Player target, HouseUser user) {
        if (!Objects.equals(this.owner, sender.getUniqueId())) {
            sender.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this premium house!"));
            return;
        }

        if (!this.isGuest(target.getUniqueId())) {
            sender.sendMessage(Utils.f(Lang.HOUSES + "&7That player is not a guest of this premium house!"));
            return;
        }

        if (user.isInsidePremiumHouse(this.id))
            user.teleportInOrOutPremiumHouse(target, this);

        sender.sendMessage(Lang.HOUSES.f("&7You removed &a" + target.getName() + "&7 as a guest from premium house &a" + this.id + '!'));
        target.sendMessage(Lang.HOUSES.f("&a" + sender.getName() + "&7 removed you as a guest from premium house &a" + this.id + "&7!"));
        this.removeGuest(target.getUniqueId());//TODO Make sure this method updates database.
    }

    public void removeGuest(Player sender, String guest) {
        if (!Objects.equals(this.owner, sender.getUniqueId())) {
            sender.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this premium house!"));
            return;
        }

        if (!this.isGuest(guest)) {
            sender.sendMessage(Utils.f(Lang.HOUSES + "&7That player is not a guest of this premium house!"));
            return;
        }

        this.removeGuest(guest);//TODO Make sure this method updates database.
        sender.sendMessage(Lang.HOUSES.f("&7You removed &a" + guest + "&7 as a guest from premium house &a" + this.id + "!&"));
    }

    public void removeGuest(UUID uuid) {
        this.guests.remove(this.getGuest(uuid));

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.deleteHouseGuest(connection, this.uniqueIdentifier, uuid);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeGuest(String name) {
        removeGuest(this.getGuest(name).getUuid());
    }

    public Collection<EditableBlock> getEditableBlocks() {
        return this.editableBlocks;
    }

    public Collection<PremiumHouseTrashcan> getTrashcans() {
        return trashcans;
    }

    public Optional<PremiumHouseTrashcan> getTrashcan(Location location) {
        if (this.trashcans.isEmpty()) return Optional.empty();
        return this.trashcans.stream().filter(trashcan -> trashcan.getLocation().equals(location)).findFirst();
    }

    public void addTrashcan(PremiumHouseTrashcan trashcan) {
        this.trashcans.add(trashcan);
    }

    public void addTrashcan(int trashcanId, int houseId, Location location, boolean owned, Callback<PremiumHouseTrashcan> callback) {
        ServerUtil.runTaskAsync(() -> {
            PremiumHouseTrashcan block = new PremiumHouseTrashcan(-1, trashcanId, houseId, location, owned);

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.addTrashcan(connection, this.uniqueIdentifier, block, new JSONHelper()
                        .put("id", trashcanId).put("loc", HouseUtils.locationToString(location)).put("owned", owned));
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            this.trashcans.add(block);
            callback.call(block);
        });
    }

    public void addEditableBlock(EditableBlock block) {
        this.editableBlocks.add(block);
    }

    public void addEditableBlock(Location location, Material material, byte data, Callback<EditableBlock> callback) {
        ServerUtil.runTaskAsync(() -> {
            EditableBlock block = new EditableBlock(-1, location, material, data);

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                PremiumHouseDAO.addBlock(connection, this.uniqueIdentifier, block, new JSONHelper()
                        .put("loc", HouseUtils.locationToString(location)).put("block_data", material.name() + "," + data));
            } catch (SQLException e) {
                e.printStackTrace();
                callback.call(null);
                return;
            }

            this.editableBlocks.add(block);
            callback.call(block);
        });
    }
}
