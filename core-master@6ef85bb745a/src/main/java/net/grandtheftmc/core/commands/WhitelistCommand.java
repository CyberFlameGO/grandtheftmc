package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.whitelist.WhitelistManager;
import net.grandtheftmc.core.whitelist.WhitelistedUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WhitelistCommand implements CommandExecutor {

    void unsafeMethod() {
        throw new UnsupportedOperationException("You shouldn't call this!");
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (s instanceof Player) {
            User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
            if (!u.isAdmin()) {
                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                return true;
            }

//            unsafeMethod();
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/whitelist add/remove <player>"));
            s.sendMessage(Utils.f("&c/whitelist on/off"));
            s.sendMessage(Utils.f("&c/whitelist list"));
            return true;
        }
        WhitelistManager wm = Core.getWhitelistManager();
        switch (args[0].toLowerCase()) {
            case "add":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/whitelist add <player>"));
                    return true;
                }
                wm.whitelist(args[1]);
                s.sendMessage(Utils.f("The player &a" + args[1] + " &fwas added to the whitelist."));
                return true;
            case "remove":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/whitelist remove <player>"));
                    return true;
                }
                wm.unwhitelist(args[1]);
                s.sendMessage(Utils.f("The player &a" + args[1] + " &fwas removed from the whitelist."));
                return true;
            case "on":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/whitelist on"));
                    return true;
                }
                wm.setEnabled(true);
                s.sendMessage(Utils.f("The whitelist was enabled."));
                return true;
            case "off":
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/whitelist off"));
                    return true;
                }
                wm.setEnabled(false);
                s.sendMessage(Utils.f("The whitelist was disabled."));
                return true;
            case "list": {
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/whitelist list"));
                    return true;
                }
                List<String> whitelist = wm.getWhitelistedUsers().stream().map(WhitelistedUser::getName).collect(Collectors.toList());

                if (whitelist.isEmpty()) {
                    s.sendMessage("There are no whitelisted players!");
                    return true;
                }
                String msg = "Whitelisted players: ";
                for (String string : whitelist)
                    msg = msg + string + ", ";
                if (msg.endsWith(", "))
                    msg = msg.substring(0, msg.length() - 2);
                s.sendMessage(Utils.f(msg));
                return true;
            }
            case "bypassrank":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/whitelist bypassRank <rank/none>"));
                    return true;
                }
                if ("none".equalsIgnoreCase(args[1])) {
                    wm.setBypassRank(null);
                    s.sendMessage(Utils.f("The whitelist can no longer be bypassed with a UserRank."));
                    return true;
                }
                UserRank rank = UserRank.getUserRankOrNull(args[1]);
                if (rank == null) {
                    String msg = Lang.RANKS + "&7There is no rank with the name &a" + args[2] + "&7! Valid ranks: ";
                    for (UserRank r : UserRank.getUserRanks())
                        msg = msg + "&a" + r.getColoredName() + "&7, ";
                    if (msg.endsWith("&7, "))
                        msg = msg.substring(0, msg.length() - 4);
                    msg += "&7.";
                    s.sendMessage(Utils.f(msg));
                    return true;
                }
                wm.setBypassRank(rank);
                s.sendMessage(Utils.f("&7Players with UserRank " + rank.getColoredNameBold() + "&7 can now bypass the whitelist."));
                return true;
            default:
                s.sendMessage(Utils.f("&c/whitelist add/remove <player>"));
                s.sendMessage(Utils.f("&c/whitelist on/off"));
                s.sendMessage(Utils.f("&c/whitelist list"));
                return true;
        }

    }

}
