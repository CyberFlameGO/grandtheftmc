package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.utils.Stats;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player)s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(args.length == 0) {
            player.sendMessage(Core.getAnnouncer().getHeader());
            Stats.getInstance().getStats(player).forEach(message -> player.sendMessage(message));
            player.sendMessage(Core.getAnnouncer().getFooter());
        } else {
            if(Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                player.sendMessage(Core.getAnnouncer().getHeader());
                Stats.getInstance().getStats(target).forEach(message -> player.sendMessage(message));
                player.sendMessage(Core.getAnnouncer().getFooter());
            } else {
                player.sendMessage(Lang.GTM.f("&7Player not found!"));
            }
        }
        return true;
    }
}