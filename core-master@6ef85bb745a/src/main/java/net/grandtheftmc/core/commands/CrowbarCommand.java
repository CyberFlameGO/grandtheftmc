package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Timothy Lampen on 2017-04-24.
 */
public class CrowbarCommand implements CommandExecutor {
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        UserManager um = Core.getUserManager();
        if (args.length == 0) {
            if (!(s instanceof Player)) {
                s.sendMessage(Lang.NOTPLAYER.s());
                return true;
            }
            s.sendMessage(
                    Utils.f("&7You have &e" + um.getLoadedUser(((Player) s).getUniqueId()).getCrowbars() + " &7Crowbars!"));
            return true;
        }
        if (s instanceof Player) {
            User u = um.getLoadedUser(((Player) s).getUniqueId());
            if (!u.isAdmin() && !args[0].equalsIgnoreCase("balance")) {
                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                return true;
            }
        }
        switch (args[0].toLowerCase()) {
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/crowbar give <player> <amount>"));
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
                    user.addCrowbars(amnt);
                    user.insertLog(player, "giveCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.CROWBARS));
                    s.sendMessage(Utils.f("&7You gave &e" + amnt + " Crowbars&7 to &a" + player.getName()));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set crowbars=crowbars+" + amnt + " where lastname='" + args[1] + "';");

                ServerUtil.runTaskAsync(() -> {
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    UserDAO.addUserCrowbars(uuid, amnt);
//                    UUID uuid = UserDAO.getUuidByName(args[1]);

                    if(uuid == null) Core.log("Error while logging giveCrowbarsCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + amnt);
                    else Utils.insertLog(uuid, args[1], "giveCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
                });

                s.sendMessage(Utils.f("&cThat player is not online, so the crowbars have been forcibly updated in the database."));
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';");
//                        UUID uuid = null;
//                        String name = args[1];
//                        try {
//                            if (rs.next()) {
//                                uuid = UUID.fromString(rs.getString("uuid"));
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (uuid == null) {
//                            Core.log("Error while logging giveCrowbarsCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                        } else Utils.insertLog(uuid, name, "giveCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "giveuuid": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/crowbars give <player> <amount>"));
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
                    user.addCrowbars(amnt);
                    user.insertLog(player, "giveCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.CROWBARS));
                    s.sendMessage(Utils.f("&7You gave &e" + amnt + " Crowbars&7 to &a" + player.getName()));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set crowbars=crowbars+" + amnt + " where uuid='" + uuid + "';");

                ServerUtil.runTaskAsync(() -> {
                    UserDAO.addUserCrowbars(uuid, amnt);
                    String name = UserDAO.getNameByUuid(uuid);

                    if(name == null) Core.log("Error while logging giveCrowbarsCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
                    else Utils.insertLog(uuid, name, "giveCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
                });

                s.sendMessage(Utils.f("&cThat player is not online, so the crowbars have been forcibly updated in the database."));
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        ResultSet rs = Core.getSQL().query("select lastname from users where uuid='" + args[1] + "';");
//                        String name = null;
//                        try {
//                            if (rs.next()) {
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (name == null) {
//                            Core.log("Error while logging giveCrowbarsCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                        } else Utils.insertLog(uuid, name, "giveCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "take": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/crowbars take <player> <amount>"));
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
                    user.takeCrowbars(amnt);
                    user.insertLog(player, "takeCrowbarsCommand", "CROWBARS", amnt + " Crowbars", -amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.CROWBARS));
                    s.sendMessage(Utils.f("&7You took &e" + amnt + " Crowbars&7 to &a" + player.getName()));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set crowbars=crowbars-" + amnt + " where lastname='" + args[1] + "';");

                ServerUtil.runTaskAsync(() -> {
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    UserDAO.subtractUserCrowbars(uuid, amnt);

                    if(uuid == null) Core.log("Error while logging takeCrowbarsCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + -amnt);
                    else Utils.insertLog(uuid, args[1], "takeCrowbarsCommand", "CROWBARS", amnt + " Crowbars", -amnt, 0);
                });

                s.sendMessage(Utils.f("&cThat player is not online, so the crowbars have been forcibly updated in the database."));
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';");
//                        UUID uuid = null;
//                        String name = args[1];
//                        try {
//                            if (rs.next()) {
//                                uuid = UUID.fromString(rs.getString("uuid"));
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (uuid == null) {
//                            Core.log("Error while logging takeCrowbarsCommand for uuid " + uuid + ", name " + name + ", amnt " + -amnt);
//                        } else Utils.insertLog(uuid, name, "takeCrowbarsCommand", "CROWBARS", amnt + " Crowbars", -amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "set": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/crowbars set <player> <amount>"));
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
                    user.setCrowbars(amnt);
                    user.insertLog(player, "setCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.CROWBARS));
                    s.sendMessage(Utils.f("&7You set &a" + player.getName() + "&7's Crowbars to &e" + amnt + "&7."));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set crowbars=" + amnt + " where lastname='" + args[1] + "';");

                ServerUtil.runTaskAsync(() -> {
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    UserDAO.updateUserCrowbars(uuid, amnt);

                    if(uuid == null) Core.log("Error while logging setCrowbarsCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + amnt);
                    else Utils.insertLog(uuid, args[1], "setCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
                });

                s.sendMessage(Utils.f("&cThat player is not online, so the crowbars have been forcibly updated in the database."));
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';");
//                        UUID uuid = null;
//                        String name = args[1];
//                        try {
//                            if (rs.next()) {
//                                uuid = UUID.fromString(rs.getString("uuid"));
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (uuid == null) {
//                            Core.log("Error while logging setCrowbarsCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                        } else Utils.insertLog(uuid, name, "setCrowbarsCommand", "CROWBARS", amnt + " Crowbars", amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "balance":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("/crowbars balance <player>"));
                    return true;
                }
                UUID sender = ((Player) s).getUniqueId();
                String name = args[1];
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    User user = um.getLoadedUser(player.getUniqueId());
                    s.sendMessage(
                            Utils.f("&7The player &a" + player.getName() + "&7 has &a" + user.getCrowbars() + " Crowbars&7."));
                    return true;
                }
                s.sendMessage(Utils.f("&cThat player is not online, so hold on a second while we gather the information from the database."));

                ServerUtil.runTaskAsync(() -> {
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    int value = UserDAO.getUserCrowbars(uuid);
                    ServerUtil.runTask(() -> {
                        if (value == -1) Bukkit.getPlayer(sender).sendMessage(Utils.f("&cThat player does not exist in the database!"));
                        else Bukkit.getPlayer(sender).sendMessage(Utils.f("&7The player &a" + name + "&7 has &e" + value + " Crowbars&7."));
                    });
                });

//                Bukkit.getScheduler().scheduleAsyncDelayedTask(Core.getInstance(), () -> {
//                    ResultSet rs = Core.getSQL().query("select crowbars from users where lastname='" + name + "';");
//                    int i;
//                    try {
//                        i = rs.next() ? rs.getInt("bucks") : -1;
//                        rs.close();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> {
//                        if (i == -1)
//                            Bukkit.getPlayer(sender).sendMessage(Utils.f("&cThat player does not exist in the database!"));
//                        else
//                            Bukkit.getPlayer(sender).sendMessage(Utils.f("&7The player &a" + name + "&7 has &e" + i + " Crowbars&7."));
//                    });
//                });
            default:
                s.sendMessage(Utils.f("&c/crowbars balance [player]"));
                s.sendMessage(Utils.f("&c/crowbars set <player> <amount>"));
                s.sendMessage(Utils.f("&c/crowbars give <player> <amount>"));
                s.sendMessage(Utils.f("&c/crowbars take <player> <amount>"));
                return true;
        }
    }
}
