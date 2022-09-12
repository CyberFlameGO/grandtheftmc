package net.grandtheftmc.houses;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class HouseUtils {

    private HouseUtils() {
    }

    public static void openHouseMenu(Player player, House house, HouseUser user) {
        if (player == null || house == null)
            return;
        player.sendMessage(Utils.f(Lang.HOUSES + "&7Opening up the menu for house &a" + house.getId() + "&7."));
        user.setMenuHouseId(house.getId());
        MenuManager.openMenu(player, "house");

    }

    public static void openPremiumHouseMenu(Player player, PremiumHouse house, HouseUser user) {
        if (player == null || house == null)
            return;
        player.sendMessage(Utils.f(Lang.HOUSES + "&7Opening up the menu for premium house &a" + house.getId() + "&7."));
        user.setMenuHouseId(house.getId());
        MenuManager.openMenu(player, "premiumhouse");
    }

    public static void openPremiumHouseGuestMenu(Player player, PremiumHouse house, HouseUser user) {
        if (player == null || house == null)
            return;
        player.sendMessage(
                Utils.f(Lang.HOUSES + "&7Opening up the guest menu for premium house &a" + house.getId() + "&7."));
        user.setMenuHouseId(house.getId());
        MenuManager.openMenu(player, "guests");
    }

    public static void openChangeBlocksMenu(Player player, PremiumHouse house, HouseUser user) {
        if (player == null || house == null)
            return;
        player.sendMessage(
                Utils.f(Lang.HOUSES + "&7Opening up the change blocks menu for premium house &a" + house.getId() + "&7."));
        user.setMenuHouseId(house.getId());
        MenuManager.openMenu(player, "editblocks");
    }

    public static void openAddGuestMenu(Player player, PremiumHouse house) {

    }

    public static void openRemoveGuestMenu(Player player, PremiumHouse house, HouseUser user) {
        if (player == null || house == null)
            return;
        player.sendMessage(Utils
                .f(Lang.HOUSES + "&7Opening up the remove guest menu for premium house &a" + house.getId() + "&7."));
        user.setMenuHouseId(house.getId());
        MenuManager.openMenu(player, "removeguests");
    }

    public static void openHousesMenu(Player player, HouseUser user) {
        if (player == null)
            return;
        player.sendMessage(Utils.f(Lang.HOUSES + "&7Opening up the houses menu."));
        MenuManager.openMenu(player, "houses");
    }

    public static void openHelpMenu(Player player) {
        if (player == null)
            return;
        player.sendMessage(Utils.f(Lang.HOUSES + "&7Opening up the houses help menu."));
        MenuManager.openMenu(player, "houseshelp");
    }

    public static int getHouses(UserRank userRank) {
        switch (userRank) {
            case DEFAULT:
                return 0;
            case VIP:
                return 1;
            case PREMIUM:
                return 2;
            case ELITE:
                return 3;
            case SPONSOR:
                return 5;
            default:
                return 10;
        }
    }

    public static int getHouseDelay(UserRank userRank) {
        switch (userRank) {
            case DEFAULT:
                return 60;
            case VIP:
                return 50;
            case PREMIUM:
                return 40;
            case ELITE:
                return 30;
            default:
                return 12;
        }
    }

    public static Collection<Block> getBlocks(Location loc1, Location loc2) {
        Collection<Block> blocks = new ArrayList<>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public static JSONObject dataToJson(KeyVal<String, String>[] keyVal) {
        JSONObject json = new JSONObject();
        for (KeyVal<String, String> obj : keyVal) {
            json.append(obj.getKey(), obj.getVal());
        }
        return json;
    }

    public static JSONObject dataToJson(String data) {
        if (data == null) return new JSONObject();

        try {
            JSONObject object = new JSONObject(data);
            return object;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static Location getLocationFromString(String data) {
        String[] s = data.split(",");
        World w = Bukkit.getWorld(s[0]);
        double x = Double.parseDouble(s[1]), y = Double.parseDouble(s[2]), z = Double.parseDouble(s[3]);
        if (s.length > 4) {
            double yaw = Double.parseDouble(s[4]), pitch = Double.parseDouble(s[5]);
            return new Location(w, x, y, z, (float)yaw, (float)pitch);
        }
        return new Location(w, x, y, z);
    }

    public static Location[] getLocationArrayFromString(String data) {
        List<Location> locationList = Lists.newArrayList();

        for(String loc : data.split(";")) {
            String[] s = loc.split(",");
            World w = Bukkit.getWorld(s[0]);
            double x = Double.parseDouble(s[1]), y = Double.parseDouble(s[2]), z = Double.parseDouble(s[3]);
            if (s.length > 4) {
                double yaw = Double.parseDouble(s[4]), pitch = Double.parseDouble(s[5]);
                locationList.add(new Location(w, x, y, z, (float) yaw, (float) pitch));
            }
            locationList.add(new Location(w, x, y, z));
        }

        return locationList.toArray(new Location[locationList.size()]);
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static String locationsToString(List<Location> locations) {
        StringBuilder str = new StringBuilder();
        for (Location l : locations) {
            str.append(l.getWorld().getName()).append(",")
                    .append(l.getX()).append(",")
                    .append(l.getY()).append(",")
                    .append(l.getZ()).append(",")
                    .append(l.getYaw()).append(",")
                    .append(l.getPitch()).append(";");
        }

        str.setLength(str.length() - 1);
        return str.toString();
    }

    public static boolean locEqualsLoc(Location loc1, Location loc2, boolean pitchYaw) {
        if (loc1 == null || loc2 == null) return false;
        if (!loc1.getWorld().equals(loc2.getWorld())) return false;
        if (loc1.getX() == loc2.getX() && loc1.getY() == loc2.getY() && loc1.getZ() == loc2.getZ()) {
            if (pitchYaw) return loc1.getPitch() == loc2.getPitch() && loc1.getYaw() == loc2.getYaw();
            return true;
        }
        return false;
    }
}
