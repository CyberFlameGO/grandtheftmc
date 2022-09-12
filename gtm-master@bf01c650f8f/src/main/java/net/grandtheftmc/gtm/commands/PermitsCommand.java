package net.grandtheftmc.gtm.commands;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.CurrencyDAO;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;

public class PermitsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player && !s.hasPermission("command.permits")) {
            Player player = (Player) s;
            GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
            player.sendMessage(Utils.f("&aYou have &c" + user.getPermits() + " &apermits"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/permits <balance> name"));
            s.sendMessage(Utils.f("&c/permits <set/give/take> <name> <amnt>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "balance": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/houses permits <balance> name"));
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[1]);
                if (player == null) {
                    UUID senderUUID = s instanceof Player ? ((Player) s).getUniqueId() : null;
                    s.sendMessage(Utils.f("&cThat player isn't online, so please wait while the permits are pulled from the database."));

                    ServerUtil.runTaskAsync(() -> {
                        int permits = 0;

                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        if (uuid == null) {
                            ServerUtil.runTask(() -> s.sendMessage(Utils.f("&cThis player wasn't found.")));
                            return;
                        }

                        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                            
                        	permits = CurrencyDAO.getCurrency(connection, Currency.PERMIT.getServerKey(), uuid, Currency.PERMIT);
                        	
//                        	try (PreparedStatement statement = connection.prepareStatement("SELECT permits FROM " + Core.name() + " WHERE uuid=?;")) {
//                                statement.setString(1, uuid.toString());
//                                try (ResultSet result = statement.executeQuery()) {
//                                    if (result.next()) {
//                                        permits = result.getInt("permits");
//                                    }
//                                }
//                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        int finalPermits = permits;
                        ServerUtil.runTask(() -> {
                            if (senderUUID == null) {
                                Bukkit.getConsoleSender().sendMessage(args[1] + " has " + finalPermits + " Permits.");
                            } else {
                                Bukkit.getPlayer(senderUUID).sendMessage(Utils.f("&a " + args[1] + " has " + finalPermits + " Permits."));
                            }
                        });
                    });

//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ResultSet rs = Core.getSQL().query("select name,permits from " + Core.name() + " where name='" + args[1] + "';");
//                            String name = null;
//                            int permits = 0;
//                            try {
//                                if (rs.next()) {
//                                    name = rs.getString("name");
//                                    permits = rs.getInt("permits");
//                                    rs.close();
//                                } else {
//                                    rs.close();
//                                    return;
//                                }
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                            String finalName = name;
//                            int finalPermits = permits;
//                            new BukkitRunnable() {
//                                @Override
//                                public void run() {
//                                    (senderUUID == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(senderUUID)).sendMessage(Utils.f("&a " + finalName + " has " + finalPermits + " Permits."));
//                                }
//                            }.runTask(GTM.getInstance());
//                        }
//                    }.runTaskAsynchronously(GTM.getInstance());
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                s.sendMessage(Utils.f("&a" + player.getName() + " has " + user.getPermits() + " Permits."));
                return true;
            }
            case "set": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/permits <set/give/take> <name> <amnt>"));
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amount must be a number!"));
                    return true;
                }
                if (amnt < 0) {
                    s.sendMessage(Utils.f("&cThe amount must be bigger than 0!"));
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the permits are forcibly updated in the database."));

                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        if (uuid == null) {
                            ServerUtil.runTask(() -> s.sendMessage(Utils.f("&cThis player wasn't found.")));
                            return;
                        }

                        try (Connection conn = BaseDatabase.getInstance().getConnection()){
                        	CurrencyDAO.saveCurrency(conn, Currency.PERMIT.getServerKey(), uuid, Currency.PERMIT, amnt);
                        }
                        catch(Exception e){
                        	e.printStackTrace();
                        }
                        
                        //BaseDatabase.runCustomQuery("update " + Core.name() + " set permits=" + amnt + " where uuid='" + uuid.toString() + "';");
                        Utils.insertLog(uuid, args[1], "setPermitsCommand", "PERMITS", amnt + " Permits", amnt, 0);
                    });

//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set permits=" + amnt + " where name='" + args[1] + "';");
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';");
//                            UUID uuid = null;
//                            String name = args[1];
//                            try {
//                                if (rs.next()) {
//                                    uuid = UUID.fromString(rs.getString("uuid"));
//                                    name = rs.getString("lastname");
//                                }
//                                rs.close();
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                            if (uuid == null) {
//                                Core.log("Error while logging setPermitsCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                            } else
//                                Utils.insertLog(uuid, name, "setPermitsCommand", "PERMITS", amnt + " Permits", amnt, 0);
//
//                        }
//                    }.runTaskAsynchronously(Core.getInstance());
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.setPermits(amnt);
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.insertLog(player, "setPermitsCommand", "PERMITS", amnt + " Permits", amnt, 0);
                s.sendMessage(Utils.f("&a" + player.getName() + " now has " + user.getPermits() + " permits!"));
                return true;
            }
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/houses permits <set/give/take> <name> <amnt>"));
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amount must be a number!"));
                    return true;
                }
                if (amnt < 0) {
                    s.sendMessage(Utils.f("&cThe amount must be bigger than 0!"));
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the permits are forcibly updated in the database."));

                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        if (uuid == null) {
                            ServerUtil.runTask(() -> s.sendMessage(Utils.f("&cThis player wasn't found.")));
                            return;
                        }
                        
                        try (Connection conn = BaseDatabase.getInstance().getConnection()){
                        	CurrencyDAO.addCurrency(conn, Currency.PERMIT.getServerKey(), uuid, Currency.PERMIT, amnt);
                        }
                        catch(Exception e){
                        	e.printStackTrace();
                        }
                        
//                        BaseDatabase.runCustomQuery("update " + Core.name() + " set permits=permits+" + amnt + " where uuid='" + uuid.toString() + "';");

//                        UUID uuid = null;
//                        String name = args[1];
//
//                        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                            try (PreparedStatement statement = connection.prepareStatement("select uuid,lastname from users where lastname='" + args[1] + "';")) {
//                                try (ResultSet result = statement.executeQuery()) {
//                                    if (result.next()) {
//                                        uuid = UUID.fromString(result.getString("uuid"));
//                                        name = result.getString("lastname");
//                                    }
//                                }
//                            }
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }

                        Utils.insertLog(uuid, args[1], "givePermitsCommand", "PERMITS", amnt + " Permits", amnt, 0);
                    });

//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set permits=permits+" + amnt + " where name='" + args[1] + "';");
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';");
//                            UUID uuid = null;
//                            String name = args[1];
//                            try {
//                                if (rs.next()) {
//                                    uuid = UUID.fromString(rs.getString("uuid"));
//                                    name = rs.getString("lastname");
//                                }
//                                rs.close();
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                            if (uuid == null) {
//                                Core.log("Error while logging givePermitsCommand for uuid " + uuid + ", name " + name + ", amnt " + amnt);
//                            } else
//                                Utils.insertLog(uuid, name, "givePermitsCommand", "PERMITS", amnt + " Permits", amnt, 0);
//
//                        }
//                    }.runTaskAsynchronously(Core.getInstance());
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.addPermits(amnt);
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.insertLog(player, "givePermitsCommand", "PERMITS", amnt + " Permits", amnt, 0);
                s.sendMessage(Utils.f("&a" + player.getName() + " now has " + user.getPermits() + " permits!"));

                return true;
            }
            case "take":
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/houses permits <set/give/take> <name> <amnt>"));
                    return true;
                }
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amount must be a number!"));
                    return true;
                }
                if (amnt < 0) {
                    s.sendMessage(Utils.f("&cThe amount must be bigger than 0!"));
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the permits are forcibly updated in the database."));

                    ServerUtil.runTaskAsync(() -> {
                        UUID uuid = UserDAO.getUuidByName(args[1]);
                        if (uuid == null) {
                            ServerUtil.runTask(() -> s.sendMessage(Utils.f("&cThis player wasn't found.")));
                            return;
                        }
                        
                        try (Connection conn = BaseDatabase.getInstance().getConnection()){
                        	CurrencyDAO.addCurrency(conn, Currency.PERMIT.getServerKey(), uuid, Currency.PERMIT, -1 * amnt);
                        }
                        catch(Exception e){
                        	e.printStackTrace();
                        }

//                        BaseDatabase.runCustomQuery("update " + Core.name() + " set permits=permits-" + amnt + " where uuid='" + uuid.toString() + "';");

//                        UUID uuid = null;
//                        String name = args[1];
//
//                        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                            try (PreparedStatement statement = connection.prepareStatement("select uuid,lastname from users where lastname='" + args[1] + "';")) {
//                                try (ResultSet result = statement.executeQuery()) {
//                                    if (result.next()) {
//                                        uuid = UUID.fromString(result.getString("uuid"));
//                                        name = result.getString("lastname");
//                                    }
//                                }
//                            }
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }

                        Utils.insertLog(uuid, args[1], "takePermitsCommand", "PERMITS", -amnt + " Permits", -amnt, 0);
                    });

//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set permits=permits-" + amnt + " where name='" + args[1] + "';");
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ResultSet rs = Core.getSQL().query("select uuid,lastname from users where lastname='" + args[1] + "';");
//                            UUID uuid = null;
//                            String name = args[1];
//                            try {
//                                if (rs.next()) {
//                                    uuid = UUID.fromString(rs.getString("uuid"));
//                                    name = rs.getString("lastname");
//                                }
//                                rs.close();
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                            if (uuid == null) {
//                                Core.log("Error while logging takePermitsCommand for uuid " + uuid + ", name " + name + ", amnt " + -amnt);
//                            } else
//                                Utils.insertLog(uuid, name, "takePermitsCommand", "PERMITS", -amnt + " Permits", -amnt, 0);
//
//                        }
//                    }.runTaskAsynchronously(Core.getInstance());
                    return true;
                }

                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.takePermits(amnt);
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.insertLog(player, "takePermitsCommand", "PERMITS", amnt + " Permits", -amnt, 0);
                s.sendMessage(Utils.f("&a" + player.getName() + " now has " + user.getPermits() + " permits!"));
                return true;
        }
        return true;
    }
}