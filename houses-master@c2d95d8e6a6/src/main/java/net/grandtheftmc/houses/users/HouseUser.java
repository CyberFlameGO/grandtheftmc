package net.grandtheftmc.houses.users;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.events.TPEvent;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.dao.HouseDAO;
import net.grandtheftmc.houses.houses.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class HouseUser {

    private final UUID uuid;
    private boolean hasUpdated;
    private List<UserHouse> houses = new ArrayList<>();

    private int insideHouse;
    private int insidePremiumHouse;
    private boolean teleporting;

    private House editingHouse;
    private PremiumHouse editingPremiumHouse;
    private boolean addingChests;
    private boolean removingChests;
    private HouseDoor addingDoor;
    private PremiumHouseDoor addingPremiumDoor;
    private boolean removingDoor;
    private int addingGuest;
    private boolean addingSigns;
    private boolean removingSigns;
    private boolean addingBlocks;
    private boolean removingBlocks;
    private boolean loadedChests = false;

    private int lastChestId = -1;
    
    private Object openChest;

    private PremiumHouseTrashcan openTrashcan;
    private boolean addingTrashcans;
    private boolean removingTrashcans;

    private int menuHouseId;

    private int maxHouses = -1;

    private Block editingBlock;
    private boolean changingBlocks;
    private Blocks lastUsedMaterial;

    public HouseUser(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void dataCheck(String name) {
        for (PremiumHouse house : Houses.getManager().getPremiumHouses()) {
            if (house.isOwner(this.uuid))
                house.setOwnerName(name);
            PremiumHouseGuest guest = house.getGuest(this.uuid);
            if (guest != null)
                guest.setName(name);
        }

        String table = Core.name();

//        Core.sql.update("update " + Core.name() + "_houses set name='" + name + "' where uuid='" + this.uuid + "';");
//        BaseDatabase.runCustomQuery("update " + Core.name() + "_houses set name='" + name + "' where uuid='" + this.uuid + "';");

        //Not used.
//        Core.sql.update("update " + Core.name() + " set name='ERROR' where name='" + name + "' and uuid!='" + this.uuid + "';");
    }

    public void loadChests(Callback<Boolean> callback) {
        new BukkitRunnable() {
            @Override public void run() {
                boolean b = true;

                try (Connection connection = BaseDatabase.getInstance().getConnection()) {

                    HouseDAO.getChestContent(connection, HouseUser.this, uuid);

//                    try (PreparedStatement statement = connection.prepareStatement("select * from " + Core.name() + "_houses_chests where uuid='" + HouseUser.this.uuid + "' LIMIT 10000;")) {
//                        try (ResultSet result = statement.executeQuery()) {
//                            while (result.next()) {
//                                int id = result.getInt("houseId");
//                                int chestId = result.getInt("chestId");
//                                UserHouse userHouse = HouseUser.this.getUserHouse(id);
//                                if (userHouse == null)
//                                    continue;
//                                UserHouseChest chest = userHouse.getChestOrNull(chestId);
//                                if (chest == null) {
//                                    userHouse.addChest(new UserHouseChest(id, chestId, GTMUtils.fromBase64(result.getString("contents"))));
//                                } else {
//                                    chest.setContents(GTMUtils.fromBase64(result.getString("contents")));
//                                }
//                            }
//                        }
//                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    b = false;
                }

//                try {
//                    ResultSet rs = Core.sql.query("select * from " + Core.name() + "_houses_chests where uuid='" + HouseUser.this.uuid + "' LIMIT 10000;");
//                    // HousesChests(uuid varchar(255), houseId integer, chestId
//                    // integer, contents blob);
//                    while (rs.next()) {
//                        int id = rs.getInt("houseId");
//                        int chestId = rs.getInt("chestId");
//                        UserHouse userHouse = HouseUser.this.getUserHouse(id);
//                        if (userHouse == null)
//                            continue;
//                        UserHouseChest chest = userHouse.getChestOrNull(chestId);
//                        if (chest == null) {
//                            userHouse.addChest(new UserHouseChest(id, chestId, GTMUtils.fromBase64(rs.getString("contents"))));
//                        } else {
//                            chest.setContents(GTMUtils.fromBase64(rs.getString("contents")));
//                        }
//                    }
//                    rs.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    b = false;
//                }

                HouseUser.this.loadedChests = true;
                callback.call(b);
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public boolean updateDataFromDb() {
        HousesManager hm = Houses.getHousesManager();
        this.houses = new ArrayList<>();
        boolean b = true;

        Optional<List<UserHouse>> optional = Optional.empty();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            optional = HouseDAO.loadUser(connection, hm, this.uuid);
        } catch (SQLException e) {
            e.printStackTrace();
            b = false;
        }

        if (!optional.isPresent()) {
            System.out.println("ERROR, OPTIONAL WASN'T PRESENT.");
        }
        else {
            optional.get().stream().forEach(userHouse -> {
                System.out.println("ID - " + userHouse.getId());
                this.houses.add(userHouse);
            });
        }

//        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//            try (PreparedStatement statement = connection.prepareStatement("select * from " + Core.name() + "_houses where uuid='" + this.uuid + "' LIMIT 500;")) {
//                try (ResultSet result = statement.executeQuery()) {
//                    while (result.next()) {
//                        int id = result.getInt("houseId");
//                        House house = hm.getHouse(id);
//                        if (house == null)
//                            continue;
//                        UserHouse userHouse = new UserHouse(this.uuid, house.getId());
//                        this.houses.add(userHouse);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            b = false;
//        }

//        try {
//            ResultSet rs = Core.sql.query("select * from " + Core.name() + "_houses where uuid='" + this.uuid + "' LIMIT 500;");
//            while (rs.next()) {
//                int id = rs.getInt("houseId");
//                House house = hm.getHouse(id);
//                if (house == null)
//                    continue;
//                UserHouse userHouse = new UserHouse(this.uuid, house.getId());
//                this.houses.add(userHouse);
//            }
//            rs.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            b = false;
//        }

        this.hasUpdated = true;
        return b;
    }

    public int getMaxHouses(Player player, User user, GTMUser gtmUser) {
        if (this.maxHouses >= 0) return this.maxHouses;
        int amnt = gtmUser.getRank().getHouses() + HouseUtils.getHouses(user.getUserRank());
        for (int i = 20; i > 0; i--)
            if (player.hasPermission("houses.own." + i)) {
                amnt += i;
                break;
            }
        for (int i = 20; i > 0; i--)
            if (player.hasPermission("houses.extra." + i))
                amnt += i;
        this.maxHouses = amnt;
        return amnt;
    }

    public boolean hasMaxHouses(Player player, User user, GTMUser gtmUser) {
        return this.getMaxHouses(player, user, gtmUser) <= (this.houses.size() + this.getPremiumHouses().size());
    }

    public UserHouse getUserHouse(int id) {
        for (UserHouse house : this.houses)
            if (house.getId() == id)
                return house;
        return null;
    }

    public UserHouse getUserHouseByUniqueId(int uniqueId) {
        for (UserHouse house : this.houses)
            if (house.getUniqueId() == uniqueId)
                return house;
        return null;
    }

    public boolean ownsHouse(int id) {
        return this.getUserHouse(id) != null;
    }

    public List<UserHouse> getHouses() {
        return this.houses;
    }

    public List<UserHouse> getHouses(int start, int end) {
        if (start < 0 || end > this.houses.size()) return this.houses;
        return this.houses.subList(start, end);
    }

    public PremiumHouse getPremiumHouse(int id) {
        for (PremiumHouse house : this.getPremiumHouses())
            if (house.getId() == id)
                return house;
        return null;
    }

    public boolean ownsPremiumHouse(int id) {
        return this.getPremiumHouse(id) != null;
    }

    public List<PremiumHouse> getPremiumHouses() {
        return Houses.getManager().getPremiumHouses().stream().filter(house -> this.uuid.equals(house.getOwner())).collect(Collectors.toList());
    }

    public List<PremiumHouse> getPremiumHouses(int start, int end) {
        if (start < 0 || end > this.getPremiumHouses().size()) {
            return this.getPremiumHouses();
        }
        return this.getPremiumHouses().subList(start, end);
    }

    public List<PremiumHouse> getPremiumHousesAsGuest() {
        return Houses.getManager().getPremiumHouses().stream().filter(house -> this.uuid.equals(house.getOwner()) || house.isGuest(this.uuid)).collect(Collectors.toList());
    }

    public List<PremiumHouse> getPremiumHousesOnlyAsGuest() {
        return Houses.getManager().getPremiumHouses().stream().filter(house -> house.isGuest(this.uuid)).collect(Collectors.toList());
    }

    public boolean isInsideHouse() {
        return this.insideHouse > 0;
    }

    public boolean isInsideHouse(int id) {
        return this.insideHouse == id;
    }

    public int getInsideHouse() {
        return this.insideHouse;
    }

    public void setInsideHouse(int i) {
        this.insideHouse = i;
    }

    public boolean isInsidePremiumHouse() {
        return this.insidePremiumHouse > 0;
    }

    public boolean isInsidePremiumHouse(int id) {
        return this.insidePremiumHouse == id;
    }

    public int getInsidePremiumHouse() {
        return this.insidePremiumHouse;
    }

    public void setInsidePremiumHouse(int i) {
        this.insidePremiumHouse = i;
    }

    public boolean isTeleporting() {
        return this.teleporting;
    }

    public void setTeleporting(boolean b) {
        this.teleporting = b;
    }

    public void updateVisibility(Player player) {
        // IF PLAYER IS IN NORMAL HOUSE -> CANT SEE ANYONE
        // IF PLAYER IS NOT IN HOUSE -> CAN SEE EVERYONE THAT IS NOT IN A NORMAL
        // HOUSE
        HouseUserManager um = Houses.getUserManager();
        boolean viewInvisible = player.hasPermission("houses.viewinvisible");
        if (this.isInsideHouse())
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!viewInvisible) player.hidePlayer(p);
                if (!p.hasPermission("houses.viewinvisible")) p.hidePlayer(player);
            }
        else {

            for (Player p : Bukkit.getOnlinePlayers()) {
                HouseUser u = um.getLoadedUser(p.getUniqueId());
                if (u.isInsideHouse()) {
                    if (!p.hasPermission("houses.viewinvisible"))
                        p.hidePlayer(player);
                    if (!viewInvisible)
                        player.hidePlayer(player);
                } else {
                    p.showPlayer(player);
                    player.showPlayer(p);
                }
            }
        }
    }


    public String getHousesString() {
        String s = "";
        Iterator<UserHouse> it = this.houses.iterator();
        while (it.hasNext())
            s = s + it.next().getId() + (it.hasNext() ? "," : "");
        return s;
    }

    public void teleportInOrOutHouse(Player player, House house) {
        this.teleportInOrOutHouse(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), house.getDoors().get(0));
    }

    public void teleportInOrOutHouse(Player player, HouseDoor door) {
        this.teleportInOrOutHouse(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), door);
    }

    public void teleportInOrOutHouse(Player player, User user, House house) {
        this.teleportInOrOutHouse(player, user, house.getDoors().get(0));
    }

    public void teleportInOrOutHouse(Player player, User user, HouseDoor door) {
        UUID finalUUID = this.uuid;
        int houseId = door.getHouseId();
        int doorId = door.getId();
        this.teleporting = true;
        int cooldown = HouseUtils.getHouseDelay(user.getUserRank());
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, cooldown + 20, 0));
        player.sendMessage(Lang.HOUSES.f("&7" + (this.isInsideHouse() ? "Leaving" : "Entering") + " your house" + (cooldown < 20 ? "." : " in &a&l" + (cooldown / 20) + " &7seconds." + (user.isSpecial() ? "" : "Buy a rank to speed it up."))));
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(finalUUID);
                if (player == null)
                    return;
                HouseUser user = Houses.getUserManager().getLoadedUser(finalUUID);
                if (!user.isTeleporting())
                    return;
                House house = Houses.getManager().getHouse(houseId);
                if (house == null)
                    return;
                HouseDoor door = house.getDoor(doorId);
                if (door == null)
                    door = house.getDoor();
                user.setTeleporting(false);
                user.setInsidePremiumHouse(-1);
                TPEvent e = new TPEvent(player, player, user.isInsideHouse() ? TPEvent.TPType.HOUSE_LEAVE : TPEvent.TPType.HOUSE_ENTER).call();
                if (e.isCancelled()) {
                    player.sendMessage(Lang.HOUSES.f(e.getCancelMessage()));
                    return;
                }
                Location tpLoc = user.isInsideHouse() ? door.getOutsideLocation() : door.getInsideLocation();
                user.setInsideHouse(user.isInsideHouse() ? -1 : houseId);
                player.teleport(new Location(tpLoc.getWorld(), tpLoc.getX(), tpLoc.getY(), tpLoc.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()), TeleportCause.END_PORTAL);
                HouseUser.this.updateVisibility(player);
                if(!loadedChests)
                    loadChests((call) -> {});
            }
        }.runTaskLater(Houses.getInstance(), cooldown);
    }

    public void teleportInOrOutPremiumHouse(Player player, PremiumHouse house) {
        this.teleportInOrOutPremiumHouse(player, house.getDoors().get(0));
    }

    public void teleportInOrOutPremiumHouse(Player player, PremiumHouseDoor door) {
        UUID finalUUID = this.uuid;
        int houseId = door.getHouseId();
        int doorId = door.getId();
        this.teleporting = true;
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(finalUUID);
                if (player == null)
                    return;
                HouseUser user = Houses.getUserManager().getLoadedUser(finalUUID);
                if (!user.isTeleporting())
                    return;
                PremiumHouse house = Houses.getHousesManager().getPremiumHouse(houseId);
                if (house == null)
                    return;
                PremiumHouseDoor door = house.getDoor(doorId);
                if (door == null)
                    door = house.getDoor();
                user.setTeleporting(false);
                user.setInsideHouse(-1);
                TPEvent e = new TPEvent(player, player, user.isInsideHouse() ? TPEvent.TPType.PREMIUM_HOUSE_LEAVE : TPEvent.TPType.PREMIUM_HOUSE_ENTER).call();
                if (e.isCancelled()) {
                    player.sendMessage(Lang.HOUSES.f(e.getCancelMessage()));
                    return;
                }
                player.playSound(player.getLocation(),
                        user.isInsidePremiumHouse() ? Sound.BLOCK_IRON_DOOR_CLOSE : Sound.BLOCK_IRON_DOOR_OPEN,
                        1.0F, 1.0F);
                Location tpLoc = user.isInsidePremiumHouse() ? door.getOutsideLocation() : door.getInsideLocation();
                user.setInsidePremiumHouse(user.isInsidePremiumHouse() ? -1 : houseId);
                player.teleport(new Location(tpLoc.getWorld(), tpLoc.getX(), tpLoc.getY(), tpLoc.getZ(), player.getLocation().getYaw(), player.getLocation().getPitch()), TeleportCause.END_PORTAL);
                HouseUser.this.updateVisibility(player);
                if (!user.isInsidePremiumHouse()) {
                    if (user.isChangingBlocks()) {
                        player.sendMessage(Lang.HOUSES.f("&7House Editing disabled"));
                        user.setChangingBlocks(false);
                    }
                }
            }
        }.runTaskLater(Houses.getInstance(), 12);
    }

    public House getEditingHouse() {
        return this.editingHouse;
    }

    public void setEditingHouse(House editingHouse) {
        this.editingHouse = editingHouse;
    }

    public PremiumHouse getEditingPremiumHouse() {
        return this.editingPremiumHouse;
    }

    public void setEditingPremiumHouse(PremiumHouse editingPremiumHouse) {
        this.editingPremiumHouse = editingPremiumHouse;
    }

    public boolean isAddingChests() {
        return this.addingChests;
    }

    public void setAddingChests(boolean addingChests) {
        this.addingChests = addingChests;
    }

    public boolean isRemovingChests() {
        return this.removingChests;
    }

    public void setRemovingChests(boolean removingChests) {
        this.removingChests = removingChests;
    }

    public HouseDoor getAddingDoor() {
        return this.addingDoor;
    }

    public boolean isAddingDoor() {
        return this.addingDoor != null;
    }

    public void setAddingDoor(HouseDoor addingDoor) {
        this.addingDoor = addingDoor;
    }

    public PremiumHouseDoor getAddingPremiumDoor() {
        return this.addingPremiumDoor;
    }

    public boolean isAddingPremiumDoor() {
        return this.addingPremiumDoor != null;
    }

    public void setAddingPremiumDoor(PremiumHouseDoor addingPremiumDoor) {
        this.addingPremiumDoor = addingPremiumDoor;
    }

    public int getMenuHouseId() {
        return this.menuHouseId;
    }

    public void setMenuHouseId(int menuHouseId) {
        this.menuHouseId = menuHouseId;
    }

    public boolean isRemovingDoor() {
        return this.removingDoor;
    }

    public void setRemovingDoor(boolean removingDoor) {
        this.removingDoor = removingDoor;
    }

    public void addHouse(House house) {
        List<UserHouseChest> chests = house.getChests().stream().map(chest -> new UserHouseChest(house.getId(), chest.getId())).collect(Collectors.toList());
        this.houses.add(new UserHouse(this.uuid, house.getUniqueIdentifier(), house.getId(), chests));
        int houseId = house.getId();
        UUID uuid = this.uuid;

        ServerUtil.runTaskAsync(() -> {
            House h = Houses.getHousesManager().getHouse(houseId);
            if (h == null) return;

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.addUser(connection, house.getUniqueIdentifier(), uuid);

                for (HouseChest chest : house.getChests()) {
                    HouseDAO.addChestContent(connection, house.getUniqueIdentifier(), chest.getId(), uuid, null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                House house = Houses.getHousesManager().getHouse(houseId);
//                if (house == null) return;
//
//                HouseDAO.addUser(connection, house.getUniqueIdentifier(), uuid);
//
//                for (HouseChest chest : house.getChests()) {
////                    BaseDatabase.runCustomQuery("insert into " + Core.name() + "_houses_chests(uuid,houseId,chestId) values ('" + uuid.toString() + "','" + house.getId() + "','" + chest.getId() + "');");
//                    HouseDAO.addChestContent(connection, house.getUniqueIdentifier(), chest.getId(), uuid, null);
//                }
//
//                BaseDatabase.runCustomQuery("insert into " + Core.name() + "_houses(uuid,houseId) values ('" + uuid + "', " + house.getId() + ");");
//
////                try {
////
////                    PreparedStatement statement = Core.sql.prepareStatement(
////                            "insert into " + Core.name() + "_houses_chests(uuid,houseId,chestId) values (?,?,?);");
////                    for (HouseChest chest : house.getChests()) {
////                        statement.setString(1, uuid.toString());
////                        statement.setInt(2, house.getId());
////                        statement.setInt(3, chest.getId());
////                        statement.executeUpdate();
////                    }
////                    statement.close();
////                } catch (SQLException e) {
////                    e.printStackTrace();
////                }
////                Core.sql.update("insert into " + Core.name() + "_houses(uuid,houseId) values ('" + uuid
////                        + "', " + house.getId() + ");");
//            }
//        }.runTaskAsynchronously(Houses.getInstance());
    }

    public void removeHouse(Player player, House house) {
        UserHouse userHouse = this.getUserHouse(house.getId());
        this.houses.remove(userHouse);
        if (this.isInsideHouse(house.getId()))
            this.teleportInOrOutHouse(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), house);
//        Core.sql.updateAsyncLater("delete from " + Core.name() + "_houses_chests where uuid='" + this.uuid
//                + "' and houseId=" + house.getId() + ';');
//        Core.sql.updateAsyncLater("delete from " + Core.name() + "_houses where uuid='" + this.uuid
//                + "' and houseId=" + house.getId() + ';');

        ServerUtil.runTaskAsync(() -> {
//            BaseDatabase.runCustomQuery("delete from " + Core.name() + "_houses_chests where uuid='" + this.uuid + "' and houseId=" + house.getId() + ';');
//            BaseDatabase.runCustomQuery("delete from " + Core.name() + "_houses where uuid='" + this.uuid + "' and houseId=" + house.getId() + ';');

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                HouseDAO.deleteOwner(connection, house.getUniqueIdentifier(), this.uuid);
                //TODO Should delete from `gtm_house_chest` automatically.
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean hasUpdated() {
        return this.hasUpdated;
    }

    public void setHasUpdated(boolean hasUpdated) {
        this.hasUpdated = hasUpdated;
    }

    public void addGuest(Player sender, PremiumHouse house) {
        if (!house.isOwner(this.uuid)) {
            sender.sendMessage(Utils.f(Lang.HOUSES.f("&7You don't own this house!")));
            return;
        }

        this.addingGuest = house.getId();
        sender.sendMessage(Lang.HOUSES.f("&7Please type the name of the player you want to add as a guest, or type &a\"quit\"&7 to quit!"));
    }

    public int getAddingGuest() {
        return this.addingGuest;
    }

    public boolean isAddingGuest() {
        return this.addingGuest > 0;
    }

    public void setAddingGuest(int addingGuest) {
        this.addingGuest = addingGuest;
    }

    public boolean isAddingSigns() {
        return this.addingSigns;
    }

    public void setAddingSigns(boolean addingSigns) {
        this.addingSigns = addingSigns;
    }

    public boolean isRemovingSigns() {
        return this.removingSigns;
    }

    public void setRemovingSigns(boolean removingSigns) {
        this.removingSigns = removingSigns;
    }

    public Block getEditingBlock() {
        return this.editingBlock;
    }

    public void setEditingBlock(Block loc) {
        this.editingBlock = loc;
    }

    public boolean isAddingBlocks() {
        return this.addingBlocks;
    }

    public void setAddingBlocks(boolean a) {
        this.addingBlocks = a;
    }

    public boolean isRemovingBlocks() {
        return removingBlocks;
    }

    public void setRemovingBlocks(boolean removingBlocks) {
        this.removingBlocks = removingBlocks;
    }

    public boolean isChangingBlocks() {
        return changingBlocks;
    }

    public void setChangingBlocks(boolean changingBlocks) {
        this.changingBlocks = changingBlocks;
    }

    public Blocks getLastUsedMaterial() {
        return lastUsedMaterial;
    }

    public void setLastUsedMaterial(Blocks lastUsedMaterial) {
        this.lastUsedMaterial = lastUsedMaterial;
    }

    public boolean isAddingTrashcans() {
        return addingTrashcans;
    }

    public void setAddingTrashcans(boolean addingTrashcans) {
        this.addingTrashcans = addingTrashcans;
    }

    public boolean isRemovingTrashcans() {
        return removingTrashcans;
    }

    public void setRemovingTrashcans(boolean removingTrashcans) {
        this.removingTrashcans = removingTrashcans;
    }

    public PremiumHouseTrashcan getOpenTrashcan() {
        return openTrashcan;
    }

    public void setOpenTrashcan(PremiumHouseTrashcan openTrashcan) {
        this.openTrashcan = openTrashcan;
    }

    public int getLastChestId() {
        return lastChestId;
    }

    public void setLastChestId(int lastChestId) {
        this.lastChestId = lastChestId;
    }
    
    public void setOpenChest(Object openChest) {
        this.openChest = openChest;
    }
    
    public boolean hasChestOpen() {
        return this.openChest != null;
    }
    
    public Object getOpenChest() {
        return this.openChest;
    }
}

