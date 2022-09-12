package net.grandtheftmc.vice.users;

import com.google.common.collect.Lists;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.AmmoType;
import net.grandtheftmc.vice.items.Head;
import net.grandtheftmc.vice.items.Kit;
import net.grandtheftmc.vice.weapon.skins.WeaponSkinDAO;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public class ViceUserDAO {

    //"select * from " + Core.name() + " where uuid='" + this.uuid + "' LIMIT 1;"
    public static boolean getGeneralViceUser(ViceUser user) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            user.unlockedWeaponSkins = WeaponSkinDAO.getUnlockedSkins(connection, user.uuid);
            user.equippedWeaponSkins = WeaponSkinDAO.getEquippedSkins(connection, user.uuid);
            
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + " WHERE `uuid`=? LIMIT 1;")) {
                statement.setString(1, user.uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if(result.next()) {
                        user.rank = ViceRank.fromString(result.getString("rank"));
                        user.copRank = CopRank.getRankOrNull(result.getString("copRank"));
                        user.lastCopSalary = result.getLong("lastCopSalary");
                        user.kills = result.getInt("kills");
                        user.deaths = result.getInt("deaths");
                        user.money = result.getDouble("money");
                        user.killStreak = result.getInt("killStreak");
                        user.bonds = result.getInt("bonds");
                        user.backpackContents = ViceUtils.fromBase64(result.getString("backpackContents"));
                        user.kitExpiries = new HashMap<>();
                        user.jailTimer = result.getInt("jailTimer");
                        try {
                            user.jailCop = result.getString("jailCop") == null ? null : UUID.fromString(result.getString("jailCop"));
                        } catch (Exception ignored) {
                        }
                        user.playtime = result.getLong("playtime");
                        user.jailCopName = result.getString("jailCopName");
                        String s = result.getString("kitExpiries");
                        user.kitExpiries = new HashMap<>();
                        if (s != null)
                            try {
                                String[] expiries = s.split(",");
                                for (String e : expiries) {
                                    String[] a = e.split(":");
                                    String kit = a[0];
                                    Kit k = Vice.getItemManager().getKit(kit);
                                    if (kit == null || a.length < 2)
                                        continue;
                                    long expiry;
                                    try {
                                        expiry = Long.parseLong(a[1]);
                                    } catch (NumberFormatException ex) {
                                        continue;
                                    }
                                    if (expiry > System.currentTimeMillis()) {
                                        user.kitExpiries.put(k.getName().toLowerCase(), expiry);
                                    }
                                }

                            } catch (Exception e) {
                                Vice.getInstance().getLogger().log(Level.ALL, "Error while loading kitExpiries for player " + result.getString("name"));
                                e.printStackTrace();
                            }
                        for (AmmoType type : AmmoType.getTypes())
                            if (!type.isInInventory())
                                user.ammo.put(type, result.getInt(type.name().toLowerCase()));
                        for (VehicleProperties properties : Vice.getWastedVehicles().getBabies().getVehicleProperties()) {
                            if (result.getBoolean(properties.getIdentifier().toLowerCase())) {
                                PersonalVehicle v = new PersonalVehicle(properties.getIdentifier());
                                user.vehicles.add(v);
                                String st = result.getString(properties.getIdentifier().toLowerCase() + ":info");
                                if (st == null) continue;
                                String[] a = st.split(":");
                                if (a == null | a.length == 0) continue;
                                v.setHealth(Double.parseDouble(a[0]));
                            }
                        }

                        //Cheat Codes
                        if(result.getBlob("cheatcodes") != null) {
                            Blob b = result.getBlob("cheatcodes");
                            String cheatCodesBlob = new String(b.getBytes(1, (int) b.length()));
                            for (String serializedCheatCode : cheatCodesBlob.split("-")) {
                                String[] split = serializedCheatCode.split("#");
                                user.cheatCodes.put(CheatCode.valueOf(split[0]), new CheatCodeState(State.valueOf(split[1]), Boolean.valueOf(split[2])));
                            }
                        }

                        User coreUser = Core.getUserManager().getLoadedUser(user.uuid);
                        for(CheatCode code : CheatCode.values()) {
                            if (coreUser.getUserRank() == code.getMinmumRank() || coreUser.getUserRank().isHigherThan(code.getMinmumRank())) {
                                if (!user.cheatCodes.containsKey(code) || user.cheatCodes.get(code).getState() == State.LOCKED) {
                                    user.cheatCodes.put(code, new CheatCodeState(code.getDefaultState(), false));
                                }
                            }
                            else {
                                if(user.cheatCodes.containsKey(code) && !user.cheatCodes.get(code).isPurchased()) {
                                    user.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));
                                }
                            }
                        }

                        for(CheatCode code : CheatCode.values())
                            if(!user.cheatCodes.containsKey(code))
                                user.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean insertViceUser(ViceUser user, String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + Core.name() + "(`uuid`,`name`,`money`) VALUES(?,?,'5000') ON DUPLICATE KEY UPDATE `name`=?;")) {
                statement.setString(1, user.uuid.toString());
                statement.setString(2, name);
                statement.setString(3, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean checkForDuplicate(ViceUser user, String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `name`=? WHERE `name`=? AND `uuid`!=?;")) {
                statement.setString(1, "ERROR");
                statement.setString(2, name);
                statement.setString(3, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setCheatCodeState(ViceUser user) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `cheatcodes`=? WHERE `uuid`=?;")) {
                statement.setString(1, CheatCode.seralizeCheatCodes(user.cheatCodes));
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setBackpackContents(UUID uuid, ItemStack[] backpackContents) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `backpackContents`=? WHERE `uuid`=?;")) {
                statement.setString(1, backpackContents == null ? null : ViceUtils.toBase64(backpackContents));
                statement.setString(2, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setBackpackContents(String name, ItemStack[] backpackContents) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `backpackContents`=? WHERE `name`=?;")) {
                statement.setString(1, backpackContents == null ? null : ViceUtils.toBase64(backpackContents));
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setRank(UUID uuid, ViceRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `rank`=? WHERE `uuid`=?;")) {
                statement.setString(1, rank.getName());
                statement.setString(2, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setRank(String name, ViceRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `rank`=? WHERE `name`=?;")) {
                statement.setString(1, rank.getName());
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setCopRank(ViceUser user, CopRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `copRank`=? WHERE `uuid`=?;")) {
                statement.setString(1, (rank == null ? null : rank.getName()));
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setLastCopSalary(ViceUser user, long value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `lastCopSalary`=? WHERE `uuid`=?;")) {
                statement.setLong(1, value);
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setKills(ViceUser user, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `kills`=? WHERE `uuid`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setDeaths(ViceUser user, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `deaths`=? WHERE `uuid`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setMoney(UUID uuid, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `money`=? WHERE `uuid`=?;")) {
                statement.setDouble(1, value);
                statement.setString(2, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setMoney(String name, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `money`=? WHERE `name`=?;")) {
                statement.setDouble(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean addMoney(String name, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `money`=money+? WHERE `name`=?;")) {
                statement.setDouble(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean takeMoney(String name, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `money`=money-? WHERE `name`=?;")) {
                statement.setDouble(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setPlaytime(ViceUser user, long value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `playtime`=? WHERE `uuid`=?;")) {
                statement.setLong(1, value);
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setKillStreak(ViceUser user, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `killStreak`=? WHERE `uuid`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setBonds(UUID uuid, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `bonds`=? WHERE `uuid`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setBonds(String name, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `bonds`=? WHERE `name`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean addBonds(String name, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `bonds`=bonds+? WHERE `name`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean takeBonds(String name, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `bonds`=bonds-? WHERE `name`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateKitExpiries(UUID uuid, String value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `kitExpiries`=? WHERE `uuid`=?;")) {
                statement.setString(1, value);
                statement.setString(2, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateKitExpiries(String name, String value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `kitExpiries`=? WHERE `name`=?;")) {
                statement.setString(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setAmmo(ViceUser user, String key, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET " + key + "=? WHERE `uuid`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean resetAllAmmo(String name, String set) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET " + set + " WHERE `uuid`=?;")) {
                statement.setString(1, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setJailTimer(ViceUser user, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `jailTimer`=? WHERE `uuid`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean resetJail(UUID uuid) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `jailTimer`=-1,`jailCop`=NULL,`jailCopName`=NULL WHERE `uuid`=?;")) {
                statement.setString(1, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean resetJail(String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `jailTimer`=-1,`jailCop`=NULL,`jailCopName`=NULL WHERE `name`=?;")) {
                statement.setString(1, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setjailed(ViceUser user, int jailTimer, UUID uuid, String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `jailTimer`=?,`jailCop`=?,`jailCopName`=? WHERE `uuid`=?;")) {
                statement.setInt(1, jailTimer);
                statement.setString(2, uuid.toString());
                statement.setString(3, name);
                statement.setString(4, user.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setVehiclePerm(UUID uuid, VehicleProperties vehicle, boolean value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `" + vehicle.getIdentifier().toLowerCase() + "`=? WHERE `uuid`=?;")) {
                statement.setBoolean(1, value);
                statement.setString(2, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setVehiclePerm(String name, VehicleProperties vehicle, boolean value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `" + vehicle.getIdentifier().toLowerCase() + "`=? WHERE `name`=?;")) {
                statement.setBoolean(1, value);
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateRankByName(String name, ViceRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `rank`=? WHERE `name`=?;")) {
                statement.setString(1, rank.getName());
                statement.setString(2, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean resetAllVehicles(String name, String set) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET " + set + " WHERE `name`=?;")) {
                statement.setString(1, name);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateVehicles(UUID uuid, VehicleProperties v, double d) {
        //"UPDATE " + Core.name() + " SET `" + v.getIdentifier().toLowerCase() + ":info`=? WHERE `uuid`=?;"
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `" + v.getIdentifier().toLowerCase() + ":info`=? WHERE `uuid`=?;")) {
                statement.setDouble(1, d);
                statement.setString(2, uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean getCops(HashMap<String, CopRank> onlineCops, HashMap<String, CopRank> offlineCops) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + " WHERE `copRank` IS NOT NULL;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        String name = result.getString("name");
                        if(onlineCops.containsKey(name))
                            continue;
                        CopRank rank  = CopRank.getRankOrNull(result.getString("copRank"));
                        offlineCops.put(name, rank);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static int countCops(CopRank rank) {
        int amount = 0;

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + " WHERE `copRank`=?;")) {
                statement.setString(1, rank.getName());
                try (ResultSet result = statement.executeQuery()) {

                    while (result.next()) {
                        amount += 1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return amount;
        }

        return amount;
    }

    public static Optional<Object[]> getMoneyAndName(String name) {
        Object[] objects = new Object[2];

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `name`,`money` FROM " + Core.name() + " WHERE `name`=?;")) {
                statement.setString(1, name);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        objects[0] = result.getString("name");
                        objects[1] = result.getDouble("money");
                    }

                    return Optional.of(objects);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<Object[]> getBondAndName(String name) {
        Object[] objects = new Object[2];

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `name`,`bonds` FROM " + Core.name() + " WHERE `name`=?;")) {
                statement.setString(1, name);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        objects[0] = result.getString("name");
                        objects[1] = result.getDouble("bonds");
                    }

                    return Optional.of(objects);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<Object[][]> getBalanceTop(int limit) {
        Object[][] objects = new Object[limit][2];

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `money`,`name` FROM `" + Core.name() + "` ORDER BY cast(`money` as double) DESC LIMIT " + limit + ";")) {
                try (ResultSet result = statement.executeQuery()) {
                    int i = 0;
                    while (result.next()) {
                        objects[i][0] = result.getString("name");
                        objects[i][1] = result.getDouble("money");
                        i++;
                    }

                    return Optional.of(objects);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static boolean createTable() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + Core.name() + "(" +
                    "uuid varchar(40) NOT NULL, " +
                    "name varchar(17) NOT NULL, " +
                    "rank varchar(255) DEFAULT 'HOBO', " +
                    "copRank varchar(366) DEFAULT NULL, " +
                    "kills int(11) default 0, " +
                    "deaths int(11) default 0, " +
                    "money double default 0, " +
                    "killStreak int(11) default 0, " +
                    "bonds int(11) default 0, " +
                    "backpackContents longtext, " +
                    "kitExpiries varchar(255), " +
                    "houses varchar(255), " +
                    "gang varchar(255), " +
                    "gangRank varchar(255) NOT NULL DEFAULT 'member', " +
                    "jailTimer int(11) DEFAULT -1, " +
                    "jailCop varchar(255) default NULL, " +
                    "jailCopName varchar(255) default NULL, " +
                    "personalVehicle varchar(255), " +
                    "cheatcodes BLOB, " +
                    "PRIMARY KEY (uuid)" +
                    ")")) {

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean insertHead(UUID sellerUUID, String sellerName, String head, long expiry) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + Core.name() + "_heads(sellerUUID, sellerName, head, expiry) values (?,?,?,?);")) {
                statement.setString(1, sellerUUID.toString());
                statement.setString(2, sellerName);
                statement.setString(3, head);
                statement.setLong(4, expiry);
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deleteHead(UUID sellerUUID, String head, long expiry) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + Core.name() + "_heads WHERE `sellerUUID`=? AND `head`=? AND `expiry`=?;")) {
                statement.setString(1, sellerUUID.toString());
                statement.setString(2, head);
                statement.setLong(3, expiry);
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static List<Head> getHeads() {
        List<Head> heads = Lists.newArrayList();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + "_heads;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        UUID sellerUUID = null;
                        UUID bidderUUID = null;
                        try {
                            sellerUUID = result.getString("sellerUUID") == null ? null : UUID.fromString(result.getString("sellerUUID"));
                            bidderUUID = result.getString("bidderUUID") == null ? null : UUID.fromString(result.getString("bidderUUID"));
                        } catch (Exception ignored) {
                        }
                        heads.add(new Head(
                                sellerUUID,
                                result.getString("sellerName"),
                                result.getString("head"),
                                result.getLong("expiry"),
                                result.getBoolean("done"),
                                result.getBoolean("paid"),
                                result.getBoolean("gaveHead"),
                                bidderUUID,
                                result.getString("bidderName"),
                                result.getDouble("bid")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return heads;
        }

        return heads;
    }

    public static boolean managePlaytime() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + " LIMIT 1;")) {
                try (ResultSet result = statement.executeQuery()) {
                    ResultSetMetaData metaData = result.getMetaData();
                    List<String> columns = new ArrayList<>();

                    for (int i = 1; i <= metaData.getColumnCount(); i++)
                        columns.add(metaData.getColumnName(i).toLowerCase());

                    if (!columns.contains("playtime"))
//                        Core.sql.updateAsyncLater("ALTER TABLE " + Core.name() + " ADD COLUMN playtime BIGINT(20) NOT NULL DEFAULT 0;");
                        runQuery("ALTER TABLE " + Core.name() + " ADD COLUMN playtime BIGINT(20) NOT NULL DEFAULT 0;");

                    for (AmmoType type : AmmoType.values())
                        if (!type.isInInventory() && !columns.contains(type.toString().toLowerCase()))
//                            Core.sql.update("alter table " + Core.name() + " add column " + type.toString().toLowerCase() + " int(11) default 0;");
                            runQuery("ALTER TABLE " + Core.name() + " ADD COLUMN " + type.toString().toLowerCase() + " INT(11) DEFAULT 0;");

                    for (VehicleProperties vehicle : Vice.getWastedVehicles().getBabies().getVehicleProperties()) {
                        if (!columns.contains(vehicle.getIdentifier().toLowerCase()))
//                            Core.sql.update("alter table " + Core.name() + " add column " + vehicle.getIdentifier().toLowerCase() + " BOOLEAN not null default 0;");
                            runQuery("ALTER TABLE " + Core.name() + " ADD COLUMN " + vehicle.getIdentifier().toLowerCase() + " BOOLEAN NOT NULL DEFAULT 0;");

                        if (!columns.contains(vehicle.getIdentifier().toLowerCase() + ":info"))
//                            Core.sql.update("alter table " + Core.name() + " add column `" + vehicle.getIdentifier().toLowerCase() + ":info` VARCHAR(255);");
                            runQuery("ALTER TABLE " + Core.name() + " ADD COLUMN `" + vehicle.getIdentifier().toLowerCase() + ":info` VARCHAR(255);");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean runQuery(String query) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
