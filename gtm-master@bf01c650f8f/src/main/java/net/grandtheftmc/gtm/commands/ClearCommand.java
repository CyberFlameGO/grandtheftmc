package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTMUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("command.clear")) {
            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }
        Player player;
        if (args.length == 0) {
            player = (Player) s;
        } else {
            if (Bukkit.getPlayer(args[0]) == null) {
                s.sendMessage(Lang.GTM.f("&cThat player is not online!"));
                return true;
            } else {
                player = Bukkit.getPlayer(args[0]);
            }
        }
        player.getInventory().iterator().forEachRemaining(itemStack -> player.getInventory().remove(itemStack));
        player.sendMessage(Lang.GTM.f("&7Your inventory has been cleared!"));
        GTMUtils.giveGameItems(player);
        return true;
    }
}
