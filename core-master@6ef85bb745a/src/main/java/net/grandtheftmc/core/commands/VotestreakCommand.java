package net.grandtheftmc.core.commands;

import java.sql.Connection;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.VoteDAO;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.ServerUtil;

public class VotestreakCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (!user.isRank(UserRank.ADMIN)) {
            player.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (args.length != 3) {
            player.sendMessage(Lang.VOTE.f("&7/votestreak set &a<player> <streak>"));
            return true;
        }
        switch (args[0]) {
            case "set":
                String target = args[1];
                int streak = 0;
                try {
                    streak = Integer.valueOf(args[2]);
                } 
                catch (NumberFormatException exception) {
                }
                
                int finalStreak = streak;
                
                player.sendMessage(Lang.VOTE.f("&cAttempt to update manually..."));
                ServerUtil.runTaskAsync(() -> {
                	try (Connection conn = BaseDatabase.getInstance().getConnection()){
                		
                		UUID uuid = UserDAO.getUUID(conn, target);
                		if (uuid != null){
                			VoteDAO.setVoteStreak(conn, uuid, finalStreak);
                		}
                	}
                	catch(Exception e){
                		e.printStackTrace();
                	}
                });
                
                return true;
            default:
                return true;
        }
    }
}
