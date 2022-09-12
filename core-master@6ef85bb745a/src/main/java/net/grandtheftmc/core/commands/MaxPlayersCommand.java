package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaxPlayersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            User u = Core.getUserManager().getLoadedUser(((Player) commandSender).getUniqueId());
            if (!u.getUserRank().isHigherThan(UserRank.ADMIN)) {
                commandSender.sendMessage(Lang.NOPERM.s());
                return true;
            }
        }
        if (strings.length != 1) {
            commandSender.sendMessage(Lang.GTM.f("&7Usage: &a/maxplayers [number]"));
            return true;
        }
        int players;
        try {
            players = Integer.parseInt(strings[0]);
        } catch (NumberFormatException exception) {
            commandSender.sendMessage(Lang.GTM.f("&cThat is not a valid number!"));
            return true;
        }
        Utils.setMaxPlayers(players);
        commandSender.sendMessage(Lang.GTM.f("&aYou successfully set the number of max players!"));
        return true;
    }

}