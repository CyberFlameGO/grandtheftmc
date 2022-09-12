package net.grandtheftmc.core.commands;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.perms.PermsManager;
import net.grandtheftmc.core.perms.RankPerms;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;

public class RankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (s instanceof Player) {
            User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
            if (!u.isRank(UserRank.ADMIN)) {
                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                return true;
            }
        }
        if (args.length == 0) {
        	s.sendMessage(Utils.f("&c/rank set <player> <rank> [serverKey]"));
        	s.sendMessage(Utils.f("&c/rank remove <player> <rank> [serverKey]"));
            s.sendMessage(Utils.f("&c/rank upgrade <player> <from> <to>"));
            s.sendMessage(Utils.f("&c/rank addperm <rank> <perm>"));
            s.sendMessage(Utils.f("&c/rank delperm <rank> <perm>"));
            s.sendMessage(Utils.f("&c/rank listperms <rank>"));
            s.sendMessage(Utils.f("&c/rank player addperm <player> <perm>"));
            s.sendMessage(Utils.f("&c/rank player delperm <player> <perm>"));
            s.sendMessage(Utils.f("&c/rank player listperms <player>"));
            s.sendMessage(Utils.f("&c/rank reload"));
            s.sendMessage(Utils.f("&c/rank save"));
            return true;
        }

        UserManager um = Core.getUserManager();
        PermsManager pm = Core.getPermsManager();
        switch (args[0].toLowerCase()) {
            case "reload":
                Core.getSettings().setPermsConfig(Utils.loadConfig("perms"));
                Core.getPermsManager().loadPerms();
                s.sendMessage(Utils.f("&7The perms config was reloaded!"));
                return true;
            case "save":
                Core.getPermsManager().savePerms(false);
                s.sendMessage(Utils.f("&7The perms config was saved!"));
                return true;
            case "set": {
                if (args.length < 3 || args.length > 4) {
                    s.sendMessage(Utils.f("&c/rank set <player> <rank> [serverKey]"));
                    return true;
                }
                
                UserRank rank = UserRank.getUserRankOrNull(args[2]);
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

                if (s instanceof Player) {
                    User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
                    if (rank.isHigherThan(UserRank.HELPOP) && !u.isRank(UserRank.MANAGER)) {
                        s.sendMessage(Utils.f("&cYou do not have permission to set that userrank"));
                        return true;
                    }
                }

//                EnjinCore.tagUser(args[1], rank.getName(), new EnjinResponse() {
//                    @Override
//                    public void callback(EnjinResult response, String user, String tag) {
//                        if (response.equals(EnjinResult.SUCCESS)) {
//                            s.sendMessage(ChatColor.GREEN + "Enjin forum rank set successfully (" + user + " -> " + tag + ")");
//                        } else {
//                            s.sendMessage(ChatColor.RED + "Failed to update Enjin form rank (" + user + " -> " + tag + "). Reason = " + response.toString() + ".");
//                        }
//                    }
//                });
                
                // determine server we are setting
                String server = rank.getServerKey();
                if (args.length == 4){
                	server = args[3];
                }
                final String serverKey = server;

                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
//                    Core.getSQL().updateAsyncLater("update users set userrank='" + rank.getName() + "' where lastname='" + args[1] + "';");
                    ServerUtil.runTaskAsync(() -> {
                    	UUID uuid = UserDAO.getUuidByName(args[1]);
                    	if (uuid != null){
                    		try (Connection conn = BaseDatabase.getInstance().getConnection()){
                    			UserDAO.saveRank(conn, serverKey, uuid, rank);
                    		}
                    		catch(Exception e){
                    			e.printStackTrace();
                    		}
                    	}
                    	
                    });
                    s.sendMessage(Utils.f(Lang.RANKS + "&7That player is not online, so his rank has been forcibly updated in the database."));
                    return true;
                }
                
                User u = um.getLoadedUser(player.getUniqueId());
                
                // specifying server key
                u.setUserRank(rank, serverKey);
                s.sendMessage(Utils.f(Lang.RANKS + u.getColoredName(player) + " &7is now a &a" + u.getUserRank().getColoredNameBold() + "&7!"));
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.RANK));
                return true;
            }
            case "remove": {
                if (args.length < 3 || args.length > 4) {
                    s.sendMessage(Utils.f("&c/rank remove <player> <rank> [serverKey]"));
                    return true;
                }
                
                UserRank rank = UserRank.getUserRankOrNull(args[2]);
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

                if (s instanceof Player) {
                    User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
                    if (rank.isHigherThan(UserRank.HELPOP) && !u.isRank(UserRank.MANAGER)) {
                        s.sendMessage(Utils.f("&cYou do not have permission to set that userrank"));
                        return true;
                    }
                }

                // determine server we are setting
                String server = rank.getServerKey();
                if (args.length == 4){
                	server = args[3];
                }
                final String serverKey = server;
                
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    ServerUtil.runTaskAsync(() -> {
                    	UUID targetUUID = UserDAO.getUuidByName(args[1]);
                    	if (targetUUID != null){
                    		try (Connection conn = BaseDatabase.getInstance().getConnection()){
                        		UserDAO.deleteRank(conn, serverKey, targetUUID, rank);
                    		}
                        	catch(Exception e){
                        		e.printStackTrace();
                        	}
                    	}
                    });
                    s.sendMessage(Utils.f(Lang.RANKS + "&7That player is not online, so his rank has been forcibly updated in the database."));
                    return true;
                }
                
                User u = um.getLoadedUser(player.getUniqueId());
                
                // a removal causes them to set back to default
                u.setUserRank(UserRank.DEFAULT, serverKey);
                s.sendMessage(Utils.f(Lang.RANKS + u.getColoredName(player) + " &7is now a &a" + u.getUserRank().getColoredNameBold() + "&7!"));
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.RANK));
                return true;
            }
            case "upgrade": {
                if (!(s instanceof ConsoleCommandSender)) {
                    s.sendMessage(Utils.f("&cThis command can only be executed from the console!"));
                    return true;
                }
                if (args.length != 4) {
                    s.sendMessage(Utils.f("&c/rank upgrade <player> <from> <to>"));
                    return true;
                }
                UserRank from = UserRank.getUserRankOrNull(args[2]);
                UserRank to = UserRank.getUserRankOrNull(args[3]);
                if (from == null || to == null) {
                    String msg = Lang.RANKS + "&7There is no rank with the name &a" + (from == null ? args[2] : args[3]) + "&7! Valid ranks: ";
                    for (UserRank r : UserRank.getUserRanks())
                        msg = msg + "&a" + r.getColoredName() + "&7, ";
                    if (msg.endsWith("&7, "))
                        msg = msg.substring(0, msg.length() - 4);
                    msg += "&7.";
                    s.sendMessage(Utils.f(msg));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
//                    Core.getSQL().updateAsyncLater("update users set userrank='" + to.getName() + "' where lastname='" + args[1] + "' and userrank='" + from.getName() + "';");
                    ServerUtil.runTaskAsync(() -> UserDAO.updateRankByNameAndRank(args[1], from, to));
                    s.sendMessage(Utils.f(Lang.RANKS + "&7That player is not online, so his rank has been forcibly updated in the database."));
                    return true;
                }
                User u = um.getLoadedUser(player.getUniqueId());
                if (u.getUserRankNonTrial() != from) {
                    s.sendMessage(Lang.RANKS.f("&7That player does not have the rank " + from.getColoredNameBold() + "&7!"));
                    return true;
                }
                u.setUserRank(to);
                Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.RANK));
                s.sendMessage(Utils.f(Lang.RANKS + u.getColoredName(player) + " &7is now a &a" + u.getUserRank().getColoredNameBold() + "&7!"));
                return true;
            }
            case "addperm": {
                if (s instanceof Player) {
                    User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
                    if (!u.isRank(UserRank.MANAGER)) {
                        s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                        return true;
                    }
                }
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/rank addperm <rank> <perm>"));
                    return true;
                }
                UserRank rank = UserRank.getUserRankOrNull(args[1]);
                if (rank == null) {
                    String msg = Lang.RANKS + "&7There is no rank with the name &a" + args[2] + "&7! Valid ranks: ";
                    for (UserRank r : UserRank.getUserRanks())
                        msg = msg + "&a" + r.getColoredNameBold() + "&7, ";
                    if (msg.endsWith("&7, "))
                        msg = msg.substring(0, msg.length() - 4);
                    msg += "&7.";
                    s.sendMessage(Utils.f(msg));
                    return true;
                }
                pm.getRankPerms(rank).addPerm(args[2]);
                s.sendMessage(Utils.f(Lang.RANKS + "&7The rank &a" + rank.getColoredNameBold() + "&7 now has the permission &a" + args[2] + "&7!"));
                return true;
            }
            case "delperm": {
                if (s instanceof Player) {
                    User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
                    if (!u.isRank(UserRank.MANAGER)) {
                        s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                        return true;
                    }
                }
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/rank delperm <rank> <perm>"));
                    return true;
                }
                UserRank rank = UserRank.getUserRankOrNull(args[1]);
                if (rank == null) {
                    String msg = Lang.RANKS + "&7There is no rank with the name &a" + args[2] + "&7! Valid ranks: ";
                    for (UserRank r : UserRank.getUserRanks())
                        msg = msg + "&a" + r.getColoredNameBold() + "&7, ";
                    if (msg.endsWith("&7, "))
                        msg = msg.substring(0, msg.length() - 4);
                    msg += "&7.";
                    s.sendMessage(Utils.f(msg));
                    return true;
                }
                pm.getRankPerms(rank).removePerm(args[2]);
                s.sendMessage(Utils.f(Lang.RANKS + "&7You removed the permission &a" + args[2] + " &7from the rank " + rank.getColoredNameBold() + "&7!"));
                return true;
            }
            case "listperms": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/rank listperms <rank>"));
                    return true;
                }
                UserRank rank = UserRank.getUserRankOrNull(args[1]);
                if (rank == null) {
                    String msg = Lang.RANKS + "&7There is no rank with the name &a" + args[2] + "&7! Valid ranks: ";
                    for (UserRank r : UserRank.getUserRanks())
                        msg = msg + "&a" + r.getColoredNameBold() + "&7, ";
                    if (msg.endsWith("&7, "))
                        msg = msg.substring(0, msg.length() - 4);
                    msg += "&7.";
                    s.sendMessage(Utils.f(msg));
                    return true;
                }
                RankPerms rankPerms = pm.getRankPerms(rank);
                if (rankPerms == null || rankPerms.getPerms().isEmpty()) {
                    s.sendMessage(Utils.f(Lang.RANKS + "&7The rank " + rank.getColoredNameBold() + "&7 has no permissions."));
                    return true;
                }
                List<String> perms = rankPerms.getPerms();
                String msg1 = "&7The rank " + rank.getColoredNameBold() + " &7has the following permissions: ";
                String msg2 = "";
                for (String perm : perms)
                    msg2 = msg2 + "&a" + perm + "&7, ";
                if (msg2.endsWith("&7, "))
                    msg2 = msg2.substring(0, msg2.length() - 4);
                msg2 += "&7.";
                s.sendMessage(Utils.f(Lang.RANKS + msg1));
                s.sendMessage(Utils.f(msg2));
                return true;
            }
            case "player":
                if (args.length == 1) {
                    s.sendMessage(Utils.f("&c/rank player addperm <player> <perm>"));
                    s.sendMessage(Utils.f("&c/rank player delperm <player> <perm>"));
                    s.sendMessage(Utils.f("&c/rank player listperms <player>"));
                    return true;
                }
                switch (args[1].toLowerCase()) {
                    case "addperm": {
                        if (s instanceof Player) {
                            User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
                            if (!u.isRank(UserRank.MANAGER)) {
                                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                                return true;
                            }
                        }
                        if (args.length != 4) {
                            s.sendMessage(Utils.f("&c/rank player addperm <player> <perm>"));
                            return true;
                        }
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null) {
                            s.sendMessage(Utils.f(Lang.RANKS + "&7That player is not online!"));
                            return true;
                        }
                        pm.addPerm(player.getUniqueId(), args[3]);
                        s.sendMessage(Utils.f(Lang.RANKS + "&7You added the perm &a" + args[3] + "&7 to the player &a" + player.getName() + '.'));
                        return true;
                    }
                    case "delperm": {
                        if (s instanceof Player) {
                            User u = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
                            if (!u.isRank(UserRank.MANAGER)) {
                                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                                return true;
                            }
                        }
                        if (args.length != 4) {
                            s.sendMessage(Utils.f("&c/rank player delperm <player> <perm>"));
                            return true;
                        }
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null) {
                            s.sendMessage(Utils.f(Lang.RANKS + "&7That player is not online!"));
                            return true;
                        }
                        pm.removePerm(player.getUniqueId(), args[3]);
                        s.sendMessage(Utils.f(Lang.RANKS + "&7You removed the perm &a" + args[3] + "&7 from the player &a" + player.getName() + '.'));
                        return true;
                    }
                    case "listperms":
                        if (args.length != 3) {
                            s.sendMessage(Utils.f("&c/rank player listperms <player>"));
                            return true;
                        }
                        Player player = Bukkit.getPlayer(args[2]);
                        if (player == null) {
                            s.sendMessage(Utils.f("&7That player is not online!"));
                            return true;
                        }
                        User u = um.getLoadedUser(player.getUniqueId());
                        List<String> perms = pm.getPerms(player.getUniqueId());
                        if (perms == null || perms.isEmpty()) {
                            s.sendMessage(Utils.f(Lang.RANKS + "&7The player &a" + player.getName() + "&7 has no permissions."));
                            return true;
                        }
                        s.sendMessage(Utils.f(Lang.RANKS + "&7The player &a" + u.getColoredName(player) + " &7has the following permissions: "));
                        String msg2 = "";
                        for (String perm : perms)
                            msg2 = msg2 + "&7" + perm + "&7, ";
                        if (msg2.endsWith("&7, "))
                            msg2 = msg2.substring(0, msg2.length() - 4);
                        msg2 += "&7.";
                        s.sendMessage(Utils.f(msg2));
                        s.sendMessage(Utils.f(Lang.RANKS + "&7The player also has all the perms from the rank &a" + u.getUserRank().getColoredNameBold() + "&7."));
                        return true;
                }
            default:
                s.sendMessage(Utils.f("&c/rank set <player> <rank>"));
                s.sendMessage(Utils.f("&c/rank upgrade <player> <from> <to>"));
                s.sendMessage(Utils.f("&c/rank addperm <rank> <perm>"));
                s.sendMessage(Utils.f("&c/rank delperm <rank> <perm>"));
                s.sendMessage(Utils.f("&c/rank listperms <rank>"));
                s.sendMessage(Utils.f("&c/rank player addperm <player> <perm>"));
                s.sendMessage(Utils.f("&c/rank player delperm <player> <perm>"));
                s.sendMessage(Utils.f("&c/rank player listperms <player>"));
                s.sendMessage(Utils.f("&c/rank reload"));
                s.sendMessage(Utils.f("&c/rank save"));
                return true;
        }
    }
}
