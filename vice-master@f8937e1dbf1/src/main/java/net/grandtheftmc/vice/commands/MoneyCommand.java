package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.ViceUserDAO;
import net.grandtheftmc.vice.utils.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/money balance <player>"));
            s.sendMessage(Utils.f("&c/money give <player> <amount>"));
            s.sendMessage(Utils.f("&c/money take <player> <amount>"));
            s.sendMessage(Utils.f("&c/money top [page] - Shows the baltop for money"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "balance": {
                if (!s.hasPermission("command.money")) {
                    s.sendMessage(Lang.NOPERM.toString());
                    return true;
                }

                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/money balance <player>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    UUID senderUUID = s instanceof Player ? ((Player) s).getUniqueId() : null;
                    s.sendMessage(Utils.f("&cThat player isn't online, so please wait while the permits are pulled from the database."));
                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            Optional<Object[]> objs = ViceUserDAO.getMoneyAndName(args[1]);
                            if (!objs.isPresent()) return;

                            String name = (String) objs.get()[0];
                            int money = (int) objs.get()[1];
//                            try(ResultSet rs = Core.getSQL().query("select name,money from " + Core.name() + " where name='" + args[1] + "';")) {
//                                if (rs.next()) {
//                                    name = rs.getString("name");
//                                    money = rs.getInt("money");
//                                    rs.close();
//                                } else {
//                                    rs.close();
//                                    return;
//                                }
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
                            String finalName = name;
                            int finalMoney = money;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    (senderUUID == null ? Bukkit.getConsoleSender() : Bukkit.getPlayer(senderUUID)).sendMessage(Lang.MONEY.f("&a " + finalName + " has $" + finalMoney));
                                }
                            }.runTask(Vice.getInstance());
                        }
                    }.runTaskAsynchronously(Vice.getInstance());
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                s.sendMessage(Utils.f(Lang.MONEY + "&a" + player.getName() + "&7 has &a$&l" + user.getMoney() + "&7!"));
                return true;
            }
            case "give": {
                if (!s.hasPermission("command.money")) {
                    s.sendMessage(Lang.NOPERM.toString());
                    return true;
                }

                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/money give <player>"));
                    return true;
                }
                double amnt;
                try {
                    amnt = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.MONEY + "&7The amount must be a number!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the money is forcibly updated in the database."));
//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set money=money+" + amnt + " where name='" + args[1] + "';");
                    ServerUtil.runTaskAsync(() -> ViceUserDAO.addMoney(args[1], amnt));
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                user.addMoney(amnt);
                ViceUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
                s.sendMessage(Utils.f(Lang.MONEY + "&7You gave &a$&l" + amnt + "&7 to &a" + player.getName() + "&7!"));
                player.sendMessage(
                        Utils.f(Lang.MONEY + "&7You were given &a$&l" + amnt + "&7 by &a" + s.getName() + "&7!"));
                return true;
            }
            case "take": {
                if (!s.hasPermission("command.money")) {
                    s.sendMessage(Lang.NOPERM.toString());
                    return true;
                }

                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/money take <player>"));
                    return true;
                }
                double amnt;
                try {
                    amnt = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.MONEY + "&7The amount must be a number!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Utils.f("&cThat player isn't online, so hold on a second while the money is forcibly updated in the database."));
//                    Core.getSQL().updateAsyncLater("update " + Core.name() + " set money=money-" + amnt + " where name='" + args[1] + "';");
                    double finalAmnt = amnt;
                    ServerUtil.runTaskAsync(() -> ViceUserDAO.takeMoney(args[1], finalAmnt));
                    return true;
                }
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                if (!user.hasMoney(amnt))
                    amnt = user.getMoney();
                user.takeMoney(amnt);
                ViceUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user);
                s.sendMessage(Utils.f(Lang.MONEY + "&7You took &c$&l" + amnt + "&7 from &a" + player.getName() + "&7!"));
                player.sendMessage(
                        Utils.f(Lang.MONEY + "&c$&l" + amnt + "&7 was taken from you by &a" + s.getName() + "&7!"));
                return true;
            }
            
            case "top":
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.s());
                    return true;
                }

                UUID uuid = ((Player) s).getUniqueId();
                new BukkitRunnable() {
                    @Override public void run() {
                        getTopBalance(10, results -> new BukkitRunnable() {
                            @Override
                            public void run() {
                                Player player = Bukkit.getPlayer(uuid);
                                if (player == null) return;
                                player.sendMessage( Lang.MONEY.f("&7Money Top:"));

                                int i = 0;
                                for (String key : results.keySet()) {
                                    i++;
                                    player.sendMessage(Utils.f("&a#&l" + (i) + "&7: &r" + key + "&7 &a" + Utils.formatMoney(results.get(key))));
                                }
                            }
                        }.runTask(Vice.getInstance()));
                    }
                }.runTaskAsynchronously(Vice.getInstance());
                return true;
            default:
                if (s.hasPermission("command.money")) {
                    s.sendMessage(Utils.f("&c/money balance <player>"));
                    s.sendMessage(Utils.f("&c/money give <player> <amount>"));
                    s.sendMessage(Utils.f("&c/money take <player> <amount>"));
                }
                s.sendMessage(Utils.f("&c/money top - Shows the baltop for money"));
                return true;
        }

    }

    private void getTopBalance(int amount, Callback<LinkedHashMap<String, Double>> callback) {
        LinkedHashMap<String, Double> results = new LinkedHashMap<>();

        Optional<Object[][]> optional = ViceUserDAO.getBalanceTop(amount);
        if(!optional.isPresent()) return;

        for(int i = 0; i < optional.get().length; i++) {
            results.put((String)optional.get()[i][0], (double)optional.get()[i][1]);
        }

        callback.call(results);

//        PreparedStatement statement = null;
//        try {
//            final String query = "SELECT `money`,`name` FROM `" + Core.name() + "` ORDER BY cast(`money` as double) DESC LIMIT " + amount + ";";
//            statement = Core.sql.prepareStatement(query);
//            ResultSet set = statement.executeQuery();
//            while(set.next()) {
//                results.put(set.getString("name"), set.getDouble("money"));
//            }
//
//            callback.call(results);
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//        finally {
//            try {
//                if(statement != null)
//                    statement.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
