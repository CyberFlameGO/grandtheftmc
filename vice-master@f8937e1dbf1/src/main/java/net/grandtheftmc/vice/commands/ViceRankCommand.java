package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceRank;
import net.grandtheftmc.vice.users.ViceUser;

import net.grandtheftmc.vice.users.ViceUserDAO;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViceRankCommand implements CommandExecutor {

    // TODO add command/support for CopRanks

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("command.ViceRank")) {
            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/ViceRank set <player> <rank>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
        case "set":
            if (args.length != 3) {
                s.sendMessage(Utils.f("&c/ViceRank set <player> <rank>"));
                return true;
            }
            ViceRank rank = ViceRank.getRankOrNull(args[2]);
            if (rank == null) {
                String msg = Lang.RANKS + "&7There is no ViceRank with the name &a" + args[2] + "&7! Valid ranks: ";
                for (ViceRank r : ViceRank.values())
                    msg = msg + "&a" + r.getColoredNameBold() + "&7, ";
                if (msg.endsWith("&7, "))
                    msg = msg.substring(0, msg.length() - 4);
                msg += "&c.";
                s.sendMessage(Utils.f(msg));
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {
//                Core.sql.updateAsyncLater("update vice set rank='" + rank.getName() + "' where name='" + args[1] + "';");
                ServerUtil.runTaskAsync(() -> ViceUserDAO.updateRankByName(args[1], rank));
                s.sendMessage(Utils.f(Lang.RANKS + "&7That player is not online, so his rank has been forcibly updated in the database."));
                return true;
            }
            ViceUser u = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            u.setRank(rank, player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
            s.sendMessage(Utils.f(Lang.RANKS + "&a" + player.getName() + " &7is now a &a" + u.getRank().getColoredNameBold() + "&7!"));
            return true;

            default:
            s.sendMessage(Utils.f("&c/ViceRank set <player> <rank>"));
            return true;
        }
    }
}
