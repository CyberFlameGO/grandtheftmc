package net.grandtheftmc.Creative.commands;

import net.grandtheftmc.Creative.Creative;
import net.grandtheftmc.Creative.users.CreativeRank;
import net.grandtheftmc.Creative.users.CreativeUser;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CreativeRankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof ConsoleCommandSender || (s instanceof Player && Core.getUserManager().getLoadedUser(((Player)s).getUniqueId()).isRank(UserRank.BUILDER)))) {
            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/creativerank set <player> <rank>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
        case "set":
            if (args.length != 3) {
                s.sendMessage(Utils.f("&c/creativerank set <player> <rank>"));
                return true;
            }
            CreativeRank rank = CreativeRank.getRankOrNull(args[2]);
            if (rank == null) {
                StringBuilder msgBuilder = new StringBuilder(Lang.RANKS + "&7There is no creativerank with the name &a" + args[2] + "&7! Valid ranks: ");
                for (CreativeRank r : CreativeRank.getCreativeRanks())
                    msgBuilder.append("&a").append(r.getColoredNameBold()).append("&7, ");
                String msg = msgBuilder.toString();
                if (msg.endsWith("&7, "))
                    msg = msg.substring(0, msg.length() - 4);
                msg += "&c.";
                s.sendMessage(Utils.f(msg));
                return true;
            }
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null) {

//                Core.sql.updateAsyncLater("update "+ Core.name()+" set rank='" + rank.getName() + "' where name='" + args[1] + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update "+ Core.name()+" set rank='" + rank.getName() + "' where name='" + args[1] + "';"));

                s.sendMessage(Utils.f(Lang.RANKS + "&7That player is not online, so his rank has been forcibly updated in the database."));
                return true;
            }
            CreativeUser u = Creative.getUserManager().getLoadedUser(player.getUniqueId());
            u.setRank(rank, player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
            s.sendMessage(Utils.f(Lang.RANKS + "&a" + player.getName() + " &7is now a &a" + u.getRank().getColoredNameBold() + "&7!"));
            return true;

            default:
            s.sendMessage(Utils.f("&c/creativerank set <player> <rank>"));
            return true;
        }
    }
}
