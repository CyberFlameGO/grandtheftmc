package net.grandtheftmc.houses.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class HouseTrashcanCommand implements CommandExecutor {

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
            s.sendMessage(Utils.f(Lang.HOUSES + "&7&lTrashCan Help"));
            s.sendMessage(Utils.f("&3/housetrashcan&7 add"));
            s.sendMessage(Utils.f("&3/housetrashcan&7 remove"));
            s.sendMessage(Utils.f("&3/housetrashcan&7 stop"));
            return true;
        }
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        PremiumHouse premiumHouse = user.getEditingPremiumHouse();
        if (premiumHouse == null) {
            s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house! Use &3/house edit &a<id>&7."));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housetrashcan add"));
                    return true;
                }
                user.setAddingTrashcans(true);
                user.setRemovingTrashcans(false);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7Right click trashcans to add them!"));
                return true;
            case "remove":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housechest add"));
                    return true;
                }
                user.setRemovingTrashcans(true);
                user.setAddingTrashcans(false);
                s.sendMessage(Utils.f(Lang.HOUSES
                        + "&7Right click trashcans to remove them!"));
                return true;
            case "stop":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/housechest stop"));
                    return true;
                }
                user.setRemovingTrashcans(false);
                user.setAddingTrashcans(false);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7You are no longer adding/removing trashcans to/from "
                        + "premium house &a" + premiumHouse.getId()));
                return true;
            default:
                s.sendMessage(Utils.f(Lang.HOUSES + "&7&lTrashCan Help"));
                s.sendMessage(Utils.f("&3/housetrashcan&7 add"));
                s.sendMessage(Utils.f("&3/housetrashcan&7 remove"));
                s.sendMessage(Utils.f("&3/housetrashcan&7 stop"));
                return true;
        }
    }
}