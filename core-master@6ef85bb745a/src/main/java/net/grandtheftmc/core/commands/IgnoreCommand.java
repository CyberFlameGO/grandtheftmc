package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Liam on 2/10/2016.
 */
public class IgnoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (lbl.equalsIgnoreCase("ignored") || args.length == 0 || (args.length > 0 && args[0].equalsIgnoreCase("list"))) {
            if (user.getIgnored().isEmpty()) {
                s.sendMessage(Lang.GTM.f("&7You are not ignoring any players!"));
                return true;
            }
            s.sendMessage(Lang.GTM.f("&7You are ignoring the following players:"));
            String st = "";
            for (String name : user.getIgnored())
                st += "&a" + name + "&7, ";
            if (st.endsWith("&7, "))
                st = st.substring(0, st.length() - 4);
            s.sendMessage(Utils.f(st));
            return true;
        }
        if (args[0].equalsIgnoreCase("clear")) {
            user.getIgnored().clear();
            user.updateIgnored();
            s.sendMessage(Lang.MSG.f("&7You are no longer ignoring anyone!"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            s.sendMessage(Utils.f(Lang.MSG + "&7That player is not online!"));
            return true;
        }
        if (Core.getUserManager().getLoadedUser(target.getUniqueId()).isRank(UserRank.HELPOP)) {
            player.sendMessage(Lang.GTM.f("&7You may not ignore staff!"));
            return true;
        }
        if (user.isIgnored(target.getName())) {
            user.removeIgnored(target.getName());
            player.sendMessage(Lang.GTM.f("&7You are no longer ignoring &a" + Core.getUserManager().getLoadedUser(target.getUniqueId()).getColoredName(target) + "&7!"));
            return true;
        }
        user.addIgnored(target.getName());
        player.sendMessage(Lang.GTM.f("&7You ignored &a" + Core.getUserManager().getLoadedUser(target.getUniqueId()).getColoredName(target) + "&7!"));
        return true;
    }

}
