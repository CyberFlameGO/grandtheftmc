package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.events.UpdateEvent.UpdateReason;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BucksCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        UserManager um = Core.getUserManager();
        if (args.length == 0) {
            if (!(s instanceof Player)) {
                s.sendMessage(Lang.NOTPLAYER.s());
                return true;
            }
            s.sendMessage(
                    Utils.f("&7You have &a" + um.getLoadedUser(((Player) s).getUniqueId()).getBucks() + " Bucks&7!"));
            return true;
        }
        if (s instanceof Player) {
            User u = um.getLoadedUser(((Player) s).getUniqueId());
            if (!u.isAdmin()) {
                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                return true;
            }
        }
        switch (args[0].toLowerCase()) {
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/bucks give <player> <amount>"));
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amount must be a numerical value!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    User user = um.getLoadedUser(player.getUniqueId());
                    user.addBucks(amnt);
                    s.sendMessage(Utils.f("&7You gave &a$" + amnt + "&7 to &a" + player.getName()));
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.BUCKS));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set bucks=bucks+" + amnt + " where lastname='" + args[1] + "';");
                ServerUtil.runTaskAsync(() -> UserDAO.addUserBucksByName(args[1], amnt));
                s.sendMessage(Utils.f("&cThat player is not online, so the bucks have been forcibly updated in the database."));
                return true;
            }
            case "giveuuid": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/bucks give <player> <amount>"));
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amount must be a numerical value!"));
                    return true;
                }
                UUID uuid = UUID.fromString(args[1]);
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    User user = um.getLoadedUser(uuid);
                    user.addBucks(amnt);
                    s.sendMessage(Utils.f("&7You gave &a$" + amnt + "&7 to &a" + player.getName()));
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.BUCKS));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set bucks=bucks+" + amnt + " where uuid='" + uuid + "';");
                ServerUtil.runTaskAsync(() -> UserDAO.addUserBucks(uuid, amnt));
                s.sendMessage(Utils.f("&7You gave &a$" + amnt + "&7 to &a" + uuid + "&7."));
                return true;
            }
            case "take": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/bucks take <player> <amount>"));
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amount must be a numerical value!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    User user = um.getLoadedUser(player.getUniqueId());
                    user.takeBucks(amnt);
                    s.sendMessage(Utils.f("&7You took &a$" + amnt + "&7 to &a" + player.getName()));
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.BUCKS));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set bucks=bucks-" + amnt + " where lastname='" + args[1] + "';");
                ServerUtil.runTaskAsync(() -> UserDAO.subtractUserBucksByName(args[1], amnt));
                s.sendMessage(Utils.f("&cThat player is not online, so the bucks have been forcibly updated in the database."));
                return true;
            }
            case "set": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/bucks set <player> <amount>"));
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amount must be a numerical value!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    User user = um.getLoadedUser(player.getUniqueId());
                    user.setBucks(amnt);
                    s.sendMessage(Utils.f("&7You set &a" + player.getName() + "&7's Bucks to &a$" + amnt + "&7."));
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.BUCKS));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set bucks=" + amnt + " where lastname='" + args[1] + "';");
                ServerUtil.runTaskAsync(() -> UserDAO.updateUserBucksByName(args[1], amnt));
                s.sendMessage(Utils.f("&cThat player is not online, so the bucks have been forcibly updated in the database."));
                return true;
            }
            case "balance":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("/bucks balance <player>"));
                    return true;
                }
                UUID sender = ((Player) s).getUniqueId();
                String name = args[1];
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    User user = um.getLoadedUser(player.getUniqueId());
                    s.sendMessage(Utils.f("&7The player &a" + player.getName() + "&7 has &a$" + user.getBucks() + " Bucks&7."));
                    return true;
                }
                s.sendMessage(Utils.f("&cThat player is not online, so hold on a second while we gather the information from the database."));

                ServerUtil.runTaskAsync(() -> {
                    int value = UserDAO.fetchUserBucksByName(name);

                    ServerUtil.runTask(() -> {
                        if(value == -1) Bukkit.getPlayer(sender).sendMessage(Utils.f("&cThat player does not exist in the database!"));
                        else Bukkit.getPlayer(sender).sendMessage(Utils.f("&7The player &a" + name + "&7 has &a$" + value + " Bucks&7."));
                    });
                });

//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
////                        ResultSet rs = Core.getSQL().query("select bucks from users where lastname='" + name + "';");
//                        int i;
//                        try {
//                            i = rs.next() ? rs.getInt("bucks") : -1;
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                            return;
//                        }
//                        new BukkitRunnable() {
//                            @Override
//                            public void run() {
//                                if (i == -1)
//                                    Bukkit.getPlayer(sender)
//                                            .sendMessage(Utils.f("&cThat player does not exist in the database!"));
//                                else
//                                    Bukkit.getPlayer(sender).sendMessage(
//                                            Utils.f("&7The player &a" + name + "&7 has &a$" + i + " Bucks&7."));
//                            }
//                        }.runTask(Core.getInstance());
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
            default:
                s.sendMessage(Utils.f("&c/bucks balance <player>"));
                s.sendMessage(Utils.f("&c/bucks set <player> <amount>"));
                s.sendMessage(Utils.f("&c/bucks give <player> <amount>"));
                s.sendMessage(Utils.f("&c/bucks take <player> <amount>"));
                return true;
        }
    }
}
