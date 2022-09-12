package net.grandtheftmc.houses.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseSign;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class HouseSignCommand implements CommandExecutor {

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
        Player player = (Player) s;
        UUID uuid = player.getUniqueId();
        if (args.length == 0) {
            s.sendMessage(Utils.f(Lang.HOUSES + "&7&lSigns Help"));
            s.sendMessage(Utils.f("&3/housesign&7 add"));
            s.sendMessage(Utils.f("&3/housesign&7 remove"));
            s.sendMessage(Utils.f("&3/housesign&7 removeall"));
            s.sendMessage(Utils.f("&3/housesign&7 stop"));
            s.sendMessage(Utils.f("&3/housesign&7 list"));
            s.sendMessage(Utils.f("&3/housesign&7 update &a<all>"));
            return true;
        }
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        House house = user.getEditingHouse();
        PremiumHouse premiumHouse = user.getEditingPremiumHouse();
        switch (args[0].toLowerCase()) {
            case "add":
                if (house == null && premiumHouse == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
                    return true;
                }
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housesign add"));
                    return true;
                }
                user.setAddingSigns(true);
                user.setRemovingSigns(false);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7Right click signs to add them!"));
                return true;
            case "remove":
                if (house == null && premiumHouse == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
                    return true;
                }
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housesign add"));
                    return true;
                }
                user.setRemovingSigns(true);
                user.setAddingChests(false);
                s.sendMessage(Utils.f(Lang.HOUSES
                        + "&7Right click signs to remove them!"));
                return true;
            case "removeall":
                if (house == null && premiumHouse == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
                    return true;
                }
                if (args.length > 2) {
                    s.sendMessage(Utils.f("&c/housesign removeall"));
                    return true;
                }
                if (args.length == 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES
                            + "&7Are you sure? This will delete all signs of this house! Type &3/housesign removeall confirm&7 to proceed..."));
                    return true;
                }
                if (!"confirm".equalsIgnoreCase(args[1])) {
                    s.sendMessage(Utils.f("&c/housesign removeall"));
                    return true;
                }
                if (house == null) {
                    premiumHouse.removeAllSigns();
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7All signs were removed from this premium house."));
                    return true;
                }
                house.removeAllSigns();
                s.sendMessage(Utils.f(Lang.HOUSES + "&7All signs were removed from this house."));
                return true;
            case "stop":
                if (house == null && premiumHouse == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
                    return true;
                }
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housesign stop"));
                    return true;
                }
                user.setRemovingSigns(false);
                user.setAddingSigns(false);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7You are no longer adding/removing signs to/from "
                        + (house == null ? "premium house &a" + premiumHouse.getId() : "house &a" + house.getId())
                        + "&7."));
                return true;
            case "list":
                if (house == null && premiumHouse == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
                    return true;
                }
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        page = 1;
                    }
                }
                int start = (page << 3) - 8;
                int end = (page << 3) - 1;

                if (house == null) {
                    int size = user.getEditingPremiumHouse().getSigns().size();
                    List<HouseSign> signs = user.getEditingPremiumHouse().getSigns(start, end > size ? size : end);
                    s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal Premium House Signs: &a" + user.getEditingPremiumHouse().getSigns().size()));
                    for (HouseSign loc : signs)
                        s.sendMessage(Utils.f(" &3&lLocation: &a" + Utils.blockLocationToString(loc.getLocation())));
                    return true;
                }

                int size = user.getEditingHouse().getSigns().size();
                List<HouseSign> signs = user.getEditingHouse().getSigns().subList(start, end > size ? size : end);
                s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal House Signs: &a" + user.getEditingHouse().getSigns().size()));
                for (HouseSign loc : signs)
                    s.sendMessage(Utils.f(" &3&lLocation: &a" + Utils.blockLocationToString(loc.getLocation())));
                return true;

            case "update":
                if (user.getEditingPremiumHouse() != null) {
                    PremiumHouse premHouse = user.getEditingPremiumHouse();
                    premHouse.updateSigns();
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You updated the signs of premium house &a" + premHouse.getId()));
                    return true;
                } else if (user.getEditingHouse() != null) {
                    house = user.getEditingHouse();
                    house.updateSigns();
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You updated the signs of house &a" + house.getId()));
                } else {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("all")) {
                            Houses.getHousesManager().getPremiumHouses().forEach(PremiumHouse::updateSigns);
                            s.sendMessage(Utils.f(Lang.HOUSES + "&7You updated the signs of &aALL &7premium houses"));
                        } else {
                            s.sendMessage(Utils.f("&3/housesign&7 update <all>"));
                        }
                        return true;
                    }
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house!"));
                }
                return true;
            default:
                s.sendMessage(Utils.f(Lang.HOUSES + "&7&lChests Help"));
                s.sendMessage(Utils.f("&3/housesign&7 add"));
                s.sendMessage(Utils.f("&3/housesign&7 remove"));
                s.sendMessage(Utils.f("&3/housesign&7 removeall"));
                s.sendMessage(Utils.f("&3/housesign&7 stop"));
                s.sendMessage(Utils.f("&3/housesign&7 list"));
                s.sendMessage(Utils.f("&3/housesign&7 update &a<all>"));
                return true;
        }
    }
}
