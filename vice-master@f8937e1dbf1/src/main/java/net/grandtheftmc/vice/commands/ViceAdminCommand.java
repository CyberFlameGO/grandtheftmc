package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ViceAdminCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        if (!s.hasPermission("command.admin")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        Player player = (Player) s;
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/viceadmin release <player>"));
            s.sendMessage(Utils.f("&c/viceadmin target <player>"));
            s.sendMessage(Utils.f("&c/viceadmin kitexpiries <player>"));
            s.sendMessage(Utils.f("&c/viceadmin trashcan"));
            s.sendMessage(Utils.f("&c/viceadmin gravity <modifier>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "trashcan": {
                ItemStack item = Utils.createItem(Material.DROPPER, "&7&lTrash Can");
                //Utils.b(ArmorUpgrade.TANK.getEnchantment().getName());
               // items.addUnsafeEnchantment(ArmorUpgrade.TANK.getEnchantment(), 1);
                for (Enchantment e : item.getEnchantments().keySet())
                    Utils.b(e.getName());
                player.getInventory().addItem(item);
                s.sendMessage(Utils.f("&7A Trash Can was added to your inventory. Place it so players can sell items in it."));
                return true;
            }
            case "kitexpiries": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/viceadmin kitexpiries <player>"));
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    s.sendMessage(Utils.f("&cThat player is not online!"));
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(p.getUniqueId());
                s.sendMessage(Utils.f("&7Player &a" + p.getName() + "&7 has the following kit expiries:"));
                for (Map.Entry<String, Long> entry : user.getKitExpiries().entrySet())
                    s.sendMessage(entry.getKey() + ": expiry " + entry.getValue() + " time left " + (entry.getValue() - System.currentTimeMillis()));
                s.sendMessage("KitExpiriesString: " + user.getKitExpiriesString());
                return true;
            }

            case "release": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/viceadmin release <player>"));
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    s.sendMessage(Utils.f("&cThat player is not online!"));
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(p.getUniqueId());
                user.setJailTimer(-1);
                p.teleport(Vice.getWorldManager().getWarpManager().getSpawn().getLocation());
                s.sendMessage(Utils.f("&aYou released " + p.getName() + " from jail!"));
                return true;
            }
            default:
                s.sendMessage(Utils.f("&c/viceadmin release <player>"));
                s.sendMessage(Utils.f("&c/viceadmin target <player>"));
                s.sendMessage(Utils.f("&c/viceadmin kitexpiries <player>"));
                s.sendMessage(Utils.f("&c/viceadmin trashcan"));
                s.sendMessage(Utils.f("&c/viceadmin gravity <modifier>"));
                return true;
        }
    }

}
