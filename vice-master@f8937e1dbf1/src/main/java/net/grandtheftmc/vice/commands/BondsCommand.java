package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.ViceUserDAO;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class BondsCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (s instanceof Player && !s.hasPermission("command.bonds")) {
            Player player = (Player) s;
            ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            player.sendMessage(Utils.f("&aYou have &c" + user.getBonds() + " &abonds"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/bonds <balance> name"));
            s.sendMessage(Utils.f("&c/bonds <set/give/take> <name> <amnt>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "balance": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/bonds <balance> name"));
                    return true;
                }
                Player player = Bukkit.getPlayerExact(args[1]);
                if (player == null) {
                    UUID senderUUID = s instanceof Player ? ((Player) s).getUniqueId() : null;
                    s.sendMessage(Utils.f("&cThat player isn't online, so please wait while the bonds are pulled from the database."));
                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            Optional<Object[]> optional = ViceUserDAO.getBondAndName(args[1]);
                            if(!optional.isPresent()) return;

//                            ResultSet rs = Core.getSQL().query("select name,bonds from " + Core.name() + " where name='" + args[1] + "';");
                            String name = (String) optional.get()[0];
                            int bonds = (int) optional.get()[1];
//                            try {
//                                if (rs.next()) {
//                                    name = rs.getString("name");
//                                    bonds = rs.getInt("bonds");
//                                    rs.close();
//                                } else {
//                                    rs.close();
//                                    return;
//                                }
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
                            String finalName = name;
                            int finalBonds = bonds;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    (senderUUID == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(senderUUID)).sendMessage(Utils.f("&a " + finalName + " has " + finalBonds + " Bonds."));
                                }
                            }.runTask(Vice.getInstance());
                        }
                    }.runTaskAsynchronously(Vice.getInstance());
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                s.sendMessage(Utils.f("&a" + player.getName() + " has " + user.getBonds() + " Bonds."));
                return true;
            }
            case "set": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/bonds <set/give/take> <name> <amnt>"));
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
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the bonds are forcibly updated in the database."));
                    ServerUtil.runTaskAsync(() -> ViceUserDAO.setBonds(args[1], amnt));
//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set bonds=" + amnt + " where name='" + args[1] + "';");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            UUID uuid = UserDAO.getUuidByName(args[1]);

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
                            if (uuid == null) Core.log("Error while logging setBondsCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + amnt);
                            else Utils.insertLog(uuid, args[1], "setBondsCommand", "BONDS", amnt + " Bonds", amnt, 0);

                        }
                    }.runTaskAsynchronously(Core.getInstance());
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                user.setBonds(amnt);
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.insertLog(player, "setBondsCommand", "BONDS", amnt + " Bonds", amnt, 0);
                s.sendMessage(Utils.f("&a" + player.getName() + " now has " + user.getBonds() + " bonds!"));
                return true;
            }
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/houses bonds <set/give/take> <name> <amnt>"));
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
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the bonds are forcibly updated in the database."));
//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set bonds=bonds+" + amnt + " where name='" + args[1] + "';");
                    ServerUtil.runTaskAsync(() -> ViceUserDAO.addBonds(args[1], amnt));

                    new BukkitRunnable() {
                        @Override public void run() {
                            UUID uuid = UserDAO.getUuidByName(args[1]);
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
                            if (uuid == null) {
                                Core.log("Error while logging giveBondsCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + amnt);
                            } else
                                Utils.insertLog(uuid, args[1], "giveBondsCommand", "BONDS", amnt + " Bonds", amnt, 0);

                        }
                    }.runTaskAsynchronously(Core.getInstance());
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                user.giveBonds(amnt);
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.insertLog(player, "giveBondsCommand", "BONDS", amnt + " Bonds", amnt, 0);
                s.sendMessage(Utils.f("&a" + player.getName() + " now has " + user.getBonds() + " bonds!"));

                return true;
            }
            case "take":
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/houses bonds <set/give/take> <name> <amnt>"));
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
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the bonds are forcibly updated in the database."));
//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set bonds=bonds-" + amnt + " where name='" + args[1] + "';");
                    ServerUtil.runTaskAsync(() -> ViceUserDAO.takeBonds(args[1], amnt));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            UUID uuid = UserDAO.getUuidByName(args[1]);
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
                            if (uuid == null) {
                                Core.log("Error while logging takeBondsCommand for uuid " + uuid + ", name " + args[1] + ", amnt " + -amnt);
                            } else
                                Utils.insertLog(uuid, args[1], "takeBondsCommand", "BONDS", -amnt + " Bonds", -amnt, 0);

                        }
                    }.runTaskAsynchronously(Core.getInstance());
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                user.takeBonds(amnt);
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.insertLog(player, "takeBondsCommand", "BONDS", amnt + " Bonds", -amnt, 0);
                s.sendMessage(Utils.f("&a" + player.getName() + " now has " + user.getBonds() + " bonds!"));
                return true;
        }
        return true;
    }
}