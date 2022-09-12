package net.grandtheftmc.houses.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseChest;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseChest;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HouseChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        UUID uuid = player.getUniqueId();
        if (!s.hasPermission("houses.admin")) {
            s.sendMessage(Utils.f("&cYou don't have permission to execute this command!"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f(Lang.HOUSES + "&7&lChests Help"));
            s.sendMessage(Utils.f("&3/housechest&7 add"));
            s.sendMessage(Utils.f("&3/housechest&7 remove"));
            s.sendMessage(Utils.f("&3/housechest&7 removeall"));
            s.sendMessage(Utils.f("&3/housechest&7 stop"));
            s.sendMessage(Utils.f("&3/housechest&7 list"));
            return true;
        }
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        House house = user.getEditingHouse();
        PremiumHouse premiumHouse = user.getEditingPremiumHouse();
        if (house == null && premiumHouse == null) {
            s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housechest add"));
                    return true;
                }
                user.setAddingChests(true);
                user.setRemovingChests(false);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7Right click chests to add them!"));
                return true;
            case "remove":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housechest add"));
                    return true;
                }
                user.setRemovingChests(true);
                user.setAddingChests(false);
                s.sendMessage(Utils.f(Lang.HOUSES
                        + "&7Right click chests to remove them! Warning: removing a chest will remove all stored items for all owners from that chest!"));
                return true;
            case "removeall":
                if (args.length > 2) {
                    s.sendMessage(Utils.f("&c/housechest removeall"));
                    return true;
                }
                if (args.length == 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES
                            + "&7Are you sure? This will delete all chests and their contents for all owners of this house! Type &3/house removeall confirm&7 to proceed..."));
                    return true;
                }
                if (!"confirm".equalsIgnoreCase(args[1])) {
                    s.sendMessage(Utils.f("&c/housechest removeall"));
                    return true;
                }
                if (house == null) {
                    premiumHouse.removeAllChests();
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7All chests were removed from this premium house."));
                    return true;
                }
                house.removeAllChests();
                s.sendMessage(Utils.f(Lang.HOUSES + "&7All chests were removed from this house."));
                return true;
            case "stop":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housechest stop"));
                    return true;
                }
                user.setRemovingChests(false);
                user.setAddingChests(false);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7You are no longer adding/removing chests to/from "
                        + (house == null ? "premium house &a" + premiumHouse.getId() : "house &a" + house.getId())
                        + "&7."));
                return true;
            case "list":
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }
                int start = (page << 3) - 7;
                int end = page << 3;
                if (house == null) {
                    List<PremiumHouseChest> chests = user.getEditingPremiumHouse().getChests(start, end);
                    s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal Premium Chests: &a"
                            + user.getEditingPremiumHouse().getChests().size()));
                    for (PremiumHouseChest chest : chests)
                        s.sendMessage(Utils.f(" &3&lID: &a" + chest.getId() + " &3&lLocation 1: &a"
                                + Utils.blockLocationToString(chest.getLoc1()) + (chest.getLoc2() == null ? "" : " &3&lLocation 2: &a" + Utils.blockLocationToString(chest.getLoc2()))));
                    return true;
                }
                List<HouseChest> chests = user.getEditingHouse().getChests(start, end);
                s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal Chests: &a"
                        + user.getEditingHouse().getChests().size()));
                for (HouseChest chest : chests)
                    s.sendMessage(Utils.f(" &3&lID: &a" + chest.getId() + " &3&lLocation 1: &a"
                            + Utils.blockLocationToString(chest.getLoc1()) + (chest.getLoc2() == null ? "" : " &3&lLocation 2: &a" + Utils.blockLocationToString(chest.getLoc2()))));
                return true;
            case "clear":
                if (args.length == 1) {
                    premiumHouse.getChests().forEach(premiumHouseChest -> {
                        premiumHouseChest.clear();
                        player.sendMessage(Lang.HOUSES.f("&7Cleared chest &a" + premiumHouseChest.getId()
                            + " &7of premium house &a" + premiumHouse.getId()));
                    });
                } else if (args[1].equalsIgnoreCase("all")) {
                    Houses.getHousesManager().getPremiumHouses().forEach(targetHouse -> {
                        targetHouse.getChests().forEach(premiumHouseChest -> {
                            premiumHouseChest.clear();
                            player.sendMessage(Lang.HOUSES.f("&7Cleared chest &a" + premiumHouseChest.getId()
                                + " &7of premium house &a" + targetHouse.getId()));
                        });
                    });
                }
                return true;
            default:
                s.sendMessage(Utils.f(Lang.HOUSES + "&7&lChests Help"));
                s.sendMessage(Utils.f("&3/housechest&7 add"));
                s.sendMessage(Utils.f("&3/housechest&7 remove"));
                s.sendMessage(Utils.f("&3/housechest&7 removeall"));
                s.sendMessage(Utils.f("&3/housechest&7 stop"));
                s.sendMessage(Utils.f("&3/housechest&7 list"));
                return true;
        }
    }
}
