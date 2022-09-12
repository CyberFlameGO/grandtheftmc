package net.grandtheftmc.vice.commands;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.dao.VoteDAO;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.AmmoType;
import net.grandtheftmc.vice.users.ViceRank;
import net.grandtheftmc.vice.users.ViceUserDAO;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class ResetCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!s.isOp()) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }

        if (args.length < 2) {
            s.sendMessage(Utils.f("&c/reset&7 <target> <category/all>"));
            s.sendMessage(Utils.f("&7Categories: tokens, bucks, votes, dailyStreak," +
                    " lastDonorReward, cosmetics, rank (ViceRank), money, bonds," +
                    " backpack, kitExpiries, jail, ammo, vehicles, inventory (includes echest)"));
            return true;
        }

        if (args.length > 2) return true;

        String name = args[0];
        Player target = Bukkit.getPlayer(name);
        UUID uuid = null;
        if (target != null) {
            name = target.getName();
            uuid = target.getUniqueId();
            target.kickPlayer("You are being reset by an admin.");
        }

        String finalName = name;
        UUID finalUniqueId = uuid;

        if ("all".equalsIgnoreCase(args[1])) {
            ServerUtil.runTaskAsync(() -> {
                if (finalUniqueId == null) {
                    UUID value = UserDAO.getUuidByName(finalName);
                    if (value != null) {
                        File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + value.toString());
                        if (file.exists()) file.delete();
                    }
                }
                else {
                    File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + finalUniqueId.toString());
                    if (file.exists()) file.delete();
                }

//                Core.sql.updateAsyncLater("update users set tokens=0, bucks=0, votes=0, voteStreak=0, lastVoteStreak=0, dailyStreak=0, lastDailyReward=0, lastDonorReward=0 where lastname='" + name + "';");
                UserDAO.reset(finalName);

//                Core.sql.updateAsyncLater("delete from cosmetics where name='" + finalName + "';");
                UserDAO.deleteFromByName(finalName, "cosmetics");

//                Core.sql.updateAsyncLater("delete from " + Core.name() + " where name='" + finalName + "';");
                UserDAO.deleteFromByName(finalName, Core.name());

//                Core.sql.updateAsyncLater("update " + Core.name() + " set backpackContents=NULL where name='" + finalName + "';");
                ViceUserDAO.setBackpackContents(finalName, null);

                ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You fully reset player &a" + finalName + "&7!")));
            });

//            if (finalName == null) {
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        ResultSet rs = Core.sql.query("select uuid from users where lastname='" + finalName + "';");
//                        UUID uuid = null;
//                        try {
//                            if (rs.next()) {
//                                uuid = UUID.fromString(rs.getString("uuid"));
//                                rs.close();
//                                return;
//                            }
//                            rs.close();
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//                        UUID uuid = UserDAO.getUuidByName(finalName);
//                        if (uuid != null) {
//                            File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + uuid);
//                            if (file.exists()) file.delete();
//                        }
//                    }
//                }.runTaskAsynchronously(Vice.getInstance());
//            } else {
//                File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + uuid);
//                if (file.exists()) file.delete();
//            }
//            Core.sql.updateAsyncLater("update users set tokens=0, bucks=0, votes=0, voteStreak=0, lastVoteStreak=0, dailyStreak=0, lastDailyReward=0, lastDonorReward=0 where lastname='" + name + "';");
//            Core.sql.updateAsyncLater("delete from cosmetics where name='" + name + "';");
//            Core.sql.updateAsyncLater("delete from " + Core.name() + " where name='" + name + "';");
//            Core.sql.updateAsyncLater("update " + Core.name() + " set backpackContents=NULL where name='" + name + "';");
//            s.sendMessage(Utils.f("&7You fully reset player &a" + name + "&7!"));
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "inventory":
                ServerUtil.runTaskAsync(() -> {
                    if (finalUniqueId == null) {
                        UUID value = UserDAO.getUuidByName(finalName);
                        if (value != null) {
                            File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + value.toString());
                            if (file.exists()) file.delete();
                        }
                    }
                    else {
                        File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + finalUniqueId.toString());
                        if (file.exists()) file.delete();
                    }

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });

//                if (uuid == null) {
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            ResultSet rs = Core.sql.query("select uuid from users where lastname='" + finalName + "';");
//                            UUID uuid = null;
//                            try {
//                                if (rs.next()) {
//                                    uuid = UUID.fromString(rs.getString("uuid"));
//                                    rs.close();
//                                    return;
//                                }
//                                rs.close();
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                            File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + uuid);
//                            if (file.exists()) file.delete();
//                        }
//                    }.runTaskAsynchronously(Vice.getInstance());
//                } else {
//                    UUID finalUuid = uuid;
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + finalUuid);
//                            if (file.exists()) file.delete();
//                        }
//                    }.runTaskAsynchronously(Vice.getInstance());
//                }
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "tokens":
//                Core.sql.updateAsyncLater("update users set tokens=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> UserDAO.updateUserTokensByName(finalName, 0));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "bucks":
//                Core.sql.updateAsyncLater("update users set bucks=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> UserDAO.updateUserBucksByName(finalName, 0));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "votes":
//                Core.sql.updateAsyncLater("update users set votes=0, voteStreak=0, lastVoteStreak=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    VoteDAO.updateVoteStreakByName(finalName, 0);
                    VoteDAO.updateUserVotesByName(finalName, 0);
                    VoteDAO.updateUserLastVoteStreakByName(finalName, 0);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
                return true;

            case "dailystreak":
//                Core.sql.updateAsyncLater("update users set dailyStreak=0, lastDailyReward=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    VoteDAO.updateUserDailyStreakByName(finalName, 0);
                    VoteDAO.updateUserLastDailyRewardByName(finalName, 0);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "lastdonorreward":
//                Core.sql.updateAsyncLater("update users set lastDonorReward=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    UserDAO.updateUserLastDonorRewardByName(finalName, 0);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "cosmetics":
//                Core.sql.updateAsyncLater("delete from cosmetics where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    UserDAO.deleteFromByName(finalName, "cosmetic");

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "rank":
//                Core.sql.updateAsyncLater("update " + Core.name() + " set rank='JUNKIE' where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.setRank(finalName, ViceRank.JUNKIE);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "money":
//                Core.sql.updateAsyncLater("update " + Core.name() + " set money=0 where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.setMoney(finalName, 0);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "bonds":
//                Core.sql.updateAsyncLater("update " + Core.name() + " set bonds=0 where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.setBonds(finalName, 0);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "backpack":
//                Core.sql.updateAsyncLater("update " + Core.name() + " set backpackContents=NULL where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.setBackpackContents(finalName, null);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "kitexpiries":
//                Core.sql.updateAsyncLater("update " + Core.name() + " set kitExpiries=null where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.updateKitExpiries(finalName, null);

                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "jail":
//                Core.sql.updateAsyncLater("update " + Core.name() + " set jailTimer=-1, jailCop=NULL, jailCopName=NULL where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.resetJail(finalName);
                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            case "ammo": {
                String[] st = {""};
                for (AmmoType type : AmmoType.values())
                    st[0] += type.getGameItemName() + "=0, ";

                if (st[0].endsWith(", "))
                    st[0] = st[0].substring(0, st[0].length() - 2);

//                Core.sql.updateAsyncLater("update " + Core.name() + " set " + st + " where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.resetAllAmmo(finalName, st[0]);
                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            }

            case "vehicles":
                String[] st = {"personalVehicle=NULL, "};
                for (VehicleProperties v : Vice.getWastedVehicles().getBabies().getVehicleProperties())
                    st[0] += '`' + v.getIdentifier().toLowerCase() + "`=0, `" + v.getIdentifier().toLowerCase() + ":info`=NULL, ";

//                Core.sql.updateAsyncLater("update " + Core.name() + " set " + st + " where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    ViceUserDAO.resetAllVehicles(finalName, st[0]);
                    ServerUtil.runTask(() -> s.sendMessage(Utils.f("&7You reset player &a" + finalName + "&7 for category &a" + args[1] + "&7!")));
                });
//                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;

            default:
                s.sendMessage(Utils.f("&c/reset&7 <target> <category/all>"));
                s.sendMessage(Utils.f("&7Categories: tokens, bucks, votes, dailyStreak," +
                        " lastDonorReward, rank (ViceRank), money, bonds, backpack, kitExpiries, jail, ammo, vehicles"));
                return true;
        }

    }
}
