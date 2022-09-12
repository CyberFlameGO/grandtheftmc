package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.events.UpdateEvent.UpdateReason;
import net.grandtheftmc.core.menus.MenuManager;
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

public class TokensCommand implements CommandExecutor {
    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        UserManager um = Core.getUserManager();
        if (args.length == 0) {
            if (!(s instanceof Player)) {
                s.sendMessage(Lang.NOTPLAYER.s());
                return true;
            }
            s.sendMessage(Utils.f("&7You have &e" + um.getLoadedUser(((Player) s).getUniqueId()).getTokens() + " Tokens&7!"));
            return true;
        }
        if (s instanceof Player) {
            if ("shop".equalsIgnoreCase(args[0])) {
                MenuManager.openMenu((Player) s, "tokenshop");
                return true;
            }
            User u = um.getLoadedUser(((Player) s).getUniqueId());
            if (!u.isAdmin()) {
                s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
                return true;
            }
        }
        switch (args[0].toLowerCase()) {
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/tokens give <player> <amount>"));
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
                    user.addTokens(amnt);
                    user.insertLog(player, "giveTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.TOKENS));
                    s.sendMessage(Utils.f("&7You gave &e" + amnt + " Tokens&7 to &a" + player.getName()));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set tokens=tokens+" + amnt + " where lastname='" + args[1] + "';");
                s.sendMessage(Utils.f("&cThat player is not online, so the tokens have been forcibly updated in the database."));

                ServerUtil.runTaskAsync(() -> {
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    UserDAO.addUserTokens(uuid, amnt);

                    if(uuid == null) Core.log("Error while logging giveTokensCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + amnt);
                    else Utils.insertLog(uuid, args[1], "giveTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
                });

//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        UUID uuid = null;
//                        String name = args[1];
//                        try (ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';")) {
//                            if (rs.next()) {
//                                uuid = UUID.fromString(rs.getString("uuid"));
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (uuid == null) {
//                            Core.log("Error while logging giveTokensCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                        } else Utils.insertLog(uuid, name, "giveTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "giveuuid": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/tokens give <player> <amount>"));
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
                    user.addTokens(amnt);
                    user.insertLog(player, "giveTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.TOKENS));
                    s.sendMessage(Utils.f("&7You gave &e" + amnt + " Tokens&7 to &a" + player.getName()));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set tokens=tokens+" + amnt + " where uuid='" + uuid + "';");
                ServerUtil.runTaskAsync(() -> {
                    UserDAO.addUserTokens(uuid, amnt);
                    String name = UserDAO.getNameByUuid(uuid);

                    if(name == null) Core.log("Error while logging giveTokensCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
                    else Utils.insertLog(uuid, name, "giveTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
                });

                s.sendMessage(Utils.f("&cThat player is not online, so the tokens have been forcibly updated in the database."));
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        String name = null;
//                        try (ResultSet rs = Core.getSQL().query("select lastname from users where uuid='" + args[1] + "';")) {
//                            if (rs.next()) {
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (name == null) {
//                            Core.log("Error while logging giveTokensCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                        } else Utils.insertLog(uuid, name, "giveTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "take": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/tokens take <player> <amount>"));
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
                    user.takeTokens(amnt);
                    user.insertLog(player, "takeTokensCommand", "TOKENS", amnt + " Tokens", -amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.TOKENS));
                    s.sendMessage(Utils.f("&7You took &e" + amnt + " Tokens&7 from &a" + player.getName()));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set tokens=tokens-" + amnt + " where lastname='" + args[1] + "';");
                ServerUtil.runTaskAsync(() -> {
//                    UserDAO.subtractUserBucksByName(args[1], amnt);
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    UserDAO.subtractUserTokens(uuid, amnt);

                    if(uuid == null) Core.log("Error while logging takeTokensCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + -amnt);
                    else Utils.insertLog(uuid, args[1], "takeTokensCommand", "TOKENS", amnt + " Tokens", -amnt, 0);
                });

                s.sendMessage(Utils.f("&cThat player is not online, so the tokens have been forcibly updated in the database."));
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        UUID uuid = null;
//                        String name = args[1];
//                        try (ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';")) {
//                            if (rs.next()) {
//                                uuid = UUID.fromString(rs.getString("uuid"));
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (uuid == null) {
//                            Core.log("Error while logging takeTokensCommand for uuid " + uuid + ", name " + name + ", amnt " + -amnt);
//                        } else Utils.insertLog(uuid, name, "takeTokensCommand", "TOKENS", amnt + " Tokens", -amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "set": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/tokens set <player> <amount>"));
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
                    user.setTokens(amnt);
                    user.insertLog(player, "setTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
                    Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateReason.TOKENS));
                    s.sendMessage(Utils.f("&7You set &a" + player.getName() + "&7's Tokens to &e" + amnt + "&7."));
                    return true;
                }
//                Core.getSQL().updateAsyncLater("update users set tokens=" + amnt + " where lastname='" + args[1] + "';");
                ServerUtil.runTaskAsync(() -> {
//                    UserDAO.updateUserTokensByName(args[1], amnt);
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    UserDAO.updateUserTokens(uuid, amnt);

                    if(uuid == null) Core.log("Error while logging setTokensCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + -amnt);
                    else Utils.insertLog(uuid, args[1], "setTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
                });

                s.sendMessage(Utils.f("&cThat player is not online, so the tokens have been forcibly updated in the database."));
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        UUID uuid = null;
//                        String name = args[1];
//                        try (ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';")) {
//                            if (rs.next()) {
//                                uuid = UUID.fromString(rs.getString("uuid"));
//                                name = rs.getString("lastname");
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        if (uuid == null) {
//                            Core.log("Error while logging setTokensCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                        } else Utils.insertLog(uuid, name, "setTokensCommand", "TOKENS", amnt + " Tokens", amnt, 0);
//
//                    }
//                }.runTaskAsynchronously(Core.getInstance());
                return true;
            }
            case "balance":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("/tokens balance <player>"));
                    return true;
                }
                UUID sender = ((Player) s).getUniqueId();
                String name = args[1];
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    User user = um.getLoadedUser(player.getUniqueId());
                    s.sendMessage(
                            Utils.f("&7The player &a" + player.getName() + "&7 has &a" + user.getTokens() + " Tokens&7."));
                    return true;
                }
                s.sendMessage(Utils.f("&cThat player is not online, so hold on a second while we gather the information from the database."));

                ServerUtil.runTaskAsync(() -> {
                    UUID uuid = UserDAO.getUuidByName(args[1]);
                    int value = UserDAO.getUserTokens(uuid);
                    ServerUtil.runTask(() -> {
                        if (value == -1) Bukkit.getPlayer(sender).sendMessage(Utils.f("&cThat player does not exist in the database!"));
                        else Bukkit.getPlayer(sender).sendMessage(Utils.f("&7The player &a" + name + "&7 has &e" + value + " Tokens&7."));
                    });
                });

//                Bukkit.getScheduler().scheduleAsyncDelayedTask(Core.getInstance(), () -> {
//                    int i;
//                    try (ResultSet rs = Core.getSQL().query("select tokens from users where lastname='" + name + "';")) {
//                        i = rs.next() ? rs.getInt("tokens") : -1;
//                        rs.close();
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                        return;
//                    }
//                    Bukkit.getScheduler().scheduleSyncDelayedTask(Core.getInstance(), () -> {
//                        if (i == -1)
//                            Bukkit.getPlayer(sender)
//                                    .sendMessage(Utils.f("&cThat player does not exist in the database!"));
//                        else
//                            Bukkit.getPlayer(sender).sendMessage(
//                                    Utils.f("&7The player &a" + name + "&7 has &e" + i + " Tokens&7."));
//                    });
//                });
            default:
                s.sendMessage(Utils.f("&c/tokens balance <player>"));
                s.sendMessage(Utils.f("&c/tokens set <player> <amount>"));
                s.sendMessage(Utils.f("&c/tokens give <player> <amount>"));
                s.sendMessage(Utils.f("&c/tokens take <player> <amount>"));
                return true;
        }
    }
}
