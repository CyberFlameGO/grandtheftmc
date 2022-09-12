package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.Objects;

public class BackpackCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (args.length == 0 || !s.hasPermission("backpack.admin")) {
            if (!(s instanceof Player)) {
                s.sendMessage(Lang.NOTPLAYER.s());
                return true;
            }
            Player player = (Player) s;
            GTM.getBackpackManager().openBackpack(player);
            return true;
        }
        Player player = (Player) s;
        if (Bukkit.getPlayer(args[0]) != null) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target.getOpenInventory() != null && Objects.equals("Backpack", ChatColor.stripColor(target.getOpenInventory().getTitle()))) {
                target.getOpenInventory().close();
            }
            Inventory backpack = GTM.getBackpackManager().getBackpack(target, true);
            player.openInventory(backpack);
            GTM.getUserManager().getLoadedUser(target.getUniqueId()).setBackpackOpen(true);
            return true;
        }
        switch (args[0]) {
            case "reset":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/backpack reset <player>"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    return true;
                }
                InventoryView inv = target.getOpenInventory();
                if (inv != null
                        && Objects.equals("Backpack", ChatColor.stripColor(inv.getTitle())))
                    target.closeInventory();
                GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
                targetGtmUser.setBackpackContents(null);
                s.sendMessage(Utils.f("&7You cleared the backpack of player &a" + target.getName() + "&7!"));
                target.sendMessage(Utils.f("&a" + player.getName() + "&7 cleared your backpack."));
                return true;
            default:
                s.sendMessage(Utils.f("&c/backpack reset <player>"));
                return true;
        }
    }
}
