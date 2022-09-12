package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/" + lbl + " <message>"));
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        Player target = Bukkit.getPlayer(user.getLastMessage());
        User targetUser = target == null ? null : Core.getUserManager().getLoadedUser(target.getUniqueId());
        if (target == null || (targetUser.isVanished(target) && !s.hasPermission("core.bypassmessages"))) {
            s.sendMessage(Lang.MSG.f("&7You have no one to reply to!"));
            return true;
        }
        if ((targetUser.isIgnored(s.getName()) || !targetUser.getPref(Pref.MESSAGES)) &&
                !s.hasPermission("core.bypassmessages")) {
            s.sendMessage(Utils.f(Lang.MSG + "&7That player has disabled PM's!"));
            return true;
        }
        String message = args[0];
        for (int i = 1; i < args.length; i++)
            message = message + ' ' + args[i];
        target.sendMessage(Utils.f(Lang.MSG + "&7[" + user.getColoredName(player) + "&7 -> me] &f") + message);
        s.sendMessage(Utils.f(Lang.MSG + "&7[me -> " + targetUser.getColoredName(target) + "&7] &f" + message));
        user.setLastMessage(target.getUniqueId());
        targetUser.setLastMessage(player.getUniqueId());
        return true;
    }

}