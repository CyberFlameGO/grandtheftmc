package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.enjin.EnjinCache;
import net.grandtheftmc.core.enjin.EnjinCore;
import net.grandtheftmc.core.enjin.data.EnjinResponse;
import net.grandtheftmc.core.enjin.data.EnjinResult;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForumRankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (s instanceof Player) {
            User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
            if (!u.isRank(UserRank.MANAGER)) {
                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                return true;
            }
        }

        if (args.length == 2 && l.equalsIgnoreCase("forumrank")) {
            String playername = args[0], targetRank = args[1];

            //We can set his rank, add a forum update for Enjin.
            EnjinCore.tagUser(playername, targetRank, new EnjinResponse() {
                @Override
                public void callback(EnjinResult response, String user, String tag) {
                    if (response.equals(EnjinResult.SUCCESS)) {
                        s.sendMessage(ChatColor.GREEN + "Enjin forum rank set successfully (" + user + " -> " + tag + ")");
                    } else {
                        s.sendMessage(ChatColor.RED + "Failed to update Enjin form rank (" + user + " -> " + tag + "). Reason = " + response.toString() + ".");

                        //try listing valid tags.
                        StringBuilder valTags = new StringBuilder();
                        for (String r : EnjinCache.getTagNames()) {
                            valTags.append(r).append(", ");
                        }

                        //Remove trailing ", "
                        valTags.setLength(valTags.length() - 2);

                        s.sendMessage(Utils.f("&cValid Tags: " + valTags.toString()));
                    }
                }
            });

            return true;
        } else {
            s.sendMessage(Utils.f("&c/forumrank <player> <rank>"));
            return false;
        }
    }
}