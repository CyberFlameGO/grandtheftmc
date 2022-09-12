package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalMuteCommand implements CommandExecutor {
    public static boolean chatMuted;

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            chatMuted = !chatMuted;
            s.sendMessage(Lang.GTM.f("&7Chat has been &a" + (chatMuted ? "muted&7!" : "unmuted&7!")));
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (!user.isRank(UserRank.SRMOD)) {
            player.sendMessage(Lang.NOPERM.toString());
            return true;
        }
        chatMuted = !chatMuted;
        player.sendMessage(Lang.GTM.f("&7Chat has been &a" + (chatMuted ? "muted&7!" : "unmuted&7!")));
        return true;
    }
}