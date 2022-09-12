package net.grandtheftmc.houses.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseDoor;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseDoor;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HouseDoorCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!s.hasPermission("houses.admin") ) {
            s.sendMessage(Utils.f("&cYou don't have permission to execute this command!"));
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f("&cYou are not a player!"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f(Lang.HOUSES + "&7&lChests Help"));
            s.sendMessage(Utils.f("&3/housedoor&7 add"));
            s.sendMessage(Utils.f("&3/housedoor&7 remove &a(id)"));
            s.sendMessage(Utils.f("&3/housedoor&7 stop"));
            s.sendMessage(Utils.f("&3/housedoor&7 removeall"));
            s.sendMessage(Utils.f("&3/housedoor&7 list"));
            return true;
        }
        Player player = (Player) s;
        UUID uuid = player.getUniqueId();
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        House house = user.getEditingHouse();
        PremiumHouse premiumHouse = user.getEditingPremiumHouse();
        if (house == null && premiumHouse == null) {
            s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
            return true;
        }
        switch (args[0].toLowerCase()) {
        case "add": {
            if (args.length != 1) {
                s.sendMessage(Utils.f("&c/housedoor add"));
                return true;
            }

            if (house == null) {
                premiumHouse.addDoor(result -> {
                    if (result == null) return;

                    ServerUtil.runTask(() -> {
                        user.setAddingPremiumDoor(result);
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7You are adding a door with id &a" + result.getId() + "&7. Please right click the door to add it. After that, right click to set the outside location and left click to set the inside location."));
                    });
                });
//                user.setAddingPremiumDoor(door);
//                s.sendMessage(Utils.f(Lang.HOUSES + "&7You are adding a door with id &a" + door.getId()
//                        + "&7. Please right click the door to add it. After that, right click to set the outside location and left click to set the inside location."));
                return true;
            }

            house.addDoor(result -> {
                if (result != null) {
                    ServerUtil.runTask(() -> {
                        user.setAddingDoor(result);
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7You are adding a door with id " + result.getId() + ". Please right click the door to add it. After that, right click to set the outside location and left click to set the inside location."));
                    });
                }
            });

//            user.setAddingDoor(door);
//            s.sendMessage(Utils.f(Lang.HOUSES + "&7You are adding a door with id " + door.getId()
//                    + ". Please right click the door to add it. After that, right click to set the outside location and left click to set the inside location."));
            return true;
        }
        case "remove":
            if (args.length == 2) {
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.HOUSES.f("&7The id must be a number!"));
                    return true;
                }
                if (house == null) {
                    PremiumHouseDoor door = premiumHouse.getDoor(id);
                    if (door == null) {
                        s.sendMessage(Lang.HOUSES.f("&7No door with id &a" + id + "&7 exists in this premium house!"));
                        return true;
                    }
                    premiumHouse.removeDoor(door);
                    player.sendMessage(Utils.f(Lang.HOUSES + "&7You removed a door with id &a" + door.getId()
                            + "&7 from premium house &a" + premiumHouse.getId() + "&7."));
                    return true;
                }
                HouseDoor door = house.getDoor(id);
                if (door == null) {
                    s.sendMessage(Lang.HOUSES.f("&7No door with id &a" + id + "&7 exists in this house!"));
                    return true;
                }
                house.removeDoor(door);
                player.sendMessage(Utils.f(Lang.HOUSES + "&7You removed a door with id &a" + door.getId()
                        + "&7 from house &a" + premiumHouse.getId() + "&7."));
                return true;

            }
            if (args.length != 1) {
                s.sendMessage(Utils.f("&c/housedoor remove"));
                return true;
            }
            user.setRemovingDoor(true);
            user.setAddingDoor(null);
            s.sendMessage(Utils.f(Lang.HOUSES + "&7Right click doors to remove them!"));
            return true;

            case "removeall":
            if (args.length > 2) {
                s.sendMessage(Utils.f("&c/housedoor removeall"));
                return true;
            }
            if (args.length == 1) {
                s.sendMessage(Utils.f(Lang.HOUSES + "&7Are you sure you want to do this? Type &3/" + label
                        + " removeall confirm&7 to proceed..."));
                return true;
            }
            if (!"confirm".equals(args[1])) {
                s.sendMessage(Utils.f("&c/housedoor removeall"));
                return true;
            }
            if (house == null) {
                premiumHouse.removeAllDoors();
                player.sendMessage(Utils.f(
                        Lang.HOUSES + "&7You removed all doors from premium house &a" + premiumHouse.getId() + "&7."));
                return true;
            }
            house.removeAllDoors();
            player.sendMessage(
                    Utils.f(Lang.HOUSES + "&7You removed all doors from house &a" + premiumHouse.getId() + "&7."));
            return true;

            case "stop":
            if (args.length != 1) {
                s.sendMessage(Utils.f("&c/housedoor add"));
                return true;
            }
            if (house == null) {
                if (user.isAddingPremiumDoor()) {
                    premiumHouse.removeDoor(user.getAddingPremiumDoor());
                    user.setAddingPremiumDoor(null);
                    s.sendMessage(Lang.HOUSES.f("&7The door you were adding was deleted from premium house &a"
                            + premiumHouse.getId() + "&7."));
                    return true;
                } else if (user.isRemovingDoor()) {
                    user.setRemovingDoor(false);
                    s.sendMessage(Lang.HOUSES.f(
                            "&7You are no longer removing doors from premium house &a" + premiumHouse.getId() + "&7."));
                    return true;
                }
                s.sendMessage(Lang.HOUSES.f("&7You are not adding/removing doors!"));
                return true;
            }
            if (user.isAddingDoor()) {
                house.removeDoor(user.getAddingDoor());
                user.setAddingDoor(null);
                s.sendMessage(
                        Lang.HOUSES.f("&7The door you were adding was deleted from house &a" + house.getId() + "&7."));
                return true;
            } else if (user.isRemovingDoor()) {
                user.setRemovingDoor(false);
                s.sendMessage(
                        Lang.HOUSES.f("&7You are no longer removing doors from house &a" + house.getId() + "&7."));
                return true;
            }
            s.sendMessage(Lang.HOUSES.f("&7You are not adding/removing doors!"));
            return true;
            case "list":
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                }
            }
            int start = (page << 3) - 7;
            int end = page << 3;
            if (house == null) {
                List<PremiumHouseDoor> doors = user.getEditingPremiumHouse().getDoors(start,
                        end);
                s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal Premium Doors: &a"
                        + premiumHouse.getDoors().size()));
                for (PremiumHouseDoor door : doors)
                    s.sendMessage(Utils.f(" &3&lID: &a" + door.getId() + " &3&lLocation: &a"
                            + Utils.blockLocationToString(door.getLocation()) + " &3&lOutside: &a"
                            + Utils.teleportLocationToString(door.getOutsideLocation()) + " &3&lInside: &a"
                            + Utils.teleportLocationToString(door.getInsideLocation())));
                return true;
            }
            List<HouseDoor> doors = user.getEditingHouse().getDoors(start,
                    end);
            s.sendMessage(
                    Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal Doors: &a" + house.getDoors().size()));
            for (HouseDoor door : doors)
                s.sendMessage(Utils.f(" &3&lID: &a" + door.getId() + " &3&lLocation: &a"
                        + Utils.blockLocationToString(door.getLocation()) + " &3&lOutside: &a"
                        + Utils.teleportLocationToString(door.getOutsideLocation()) + " &3&lInside: &a"
                        + Utils.teleportLocationToString(door.getInsideLocation())));
            return true;
            default:
            s.sendMessage(Utils.f(Lang.HOUSES + "&7&lChests Help"));
            s.sendMessage(Utils.f("&3/housedoor&7 add"));
            s.sendMessage(Utils.f("&3/housedoor&7 remove"));
            s.sendMessage(Utils.f("&3/housedoor&7 stop"));
            s.sendMessage(Utils.f("&3/housedoor&7 removeall"));
            s.sendMessage(Utils.f("&3/housedoor&7 list"));
            return true;
        }
    }
}
