package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.handlers.chat.ChatManager;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        if (args.length < 2) {
            s.sendMessage(Utils.f("&c/" + lbl + " <player> <message>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            s.sendMessage(Utils.f(Lang.MSG + "&7That player is not online!"));
            return true;
        }
        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
        if ((targetUser.isIgnored(s.getName()) || !targetUser.getPref(Pref.MESSAGES)) && !s.hasPermission("core.bypassmessages")) {
            s.sendMessage(Utils.f(Lang.MSG + "&7That player has disabled PM's!"));
            return true;
        }
        String message = args[1];
        for (int i = 2; i < args.length; i++) {
            message = message + ' ' + args[i];
        }
        String senderName;
        if (s instanceof Player) {
            Player player = (Player) s;
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            senderName = user.getColoredName(player);
            user.setLastMessage(target.getUniqueId());
            targetUser.setLastMessage(player.getUniqueId());
        } else {
            senderName = "&6&l" + s.getName();
            targetUser.setLastMessage(null);
        }
        for (String text : message.split(" ")) {
            if (ChatManager.getAdHandler().matchesAdvertisement(text)) {
                s.sendMessage(Lang.GTM.f("&7URL prohibited. Please do not attempt to advertise."));
                return true;
            }
        }
        if (targetUser.isVanished(target) && !s.hasPermission("core.bypassmessages")) {
            s.sendMessage(Utils.f(Lang.MSG + "&7That player is not online!"));
            target.sendMessage(Utils.f(Lang.MSG + "&7[" + senderName + "&7 -> me] &f") + message);
            return true;
        }
        if (s instanceof Player) {
            User senderUser = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
            Integer cooldown = senderUser.isSpecial() ? 4 : 5;
            if (ChatManager.getRepeatHandler().canChatAgain(senderUser.getUUID(), message)) {
                ChatManager.getRepeatHandler().addRecentMessage(senderUser.getUUID(), message, cooldown);
            } else {
                s.sendMessage(Lang.GTM.f("&7Please wait a few seconds before repeating that message."));
                return true;
            }
        }
        target.sendMessage(Utils.f(Lang.MSG + "&7[" + senderName + "&7 -> me] &f") + message);
        s.sendMessage(Utils.f(Lang.MSG + "&7[me -> " + targetUser.getColoredName(target) + "&7] &f") + message);
        return true;
    }

}
