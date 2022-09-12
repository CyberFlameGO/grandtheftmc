package net.grandtheftmc.gtm.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.items.AmmoType;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.PremiumHouse;

public class ResetCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!s.isOp()) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (args.length < 2) {
            s.sendMessage(Utils.f("&c/reset&7 [player] [category/all] <server>"));
            s.sendMessage(Utils.f("&7Categories: tokens, bucks, votes, dailyStreak," +
                    " lastDonorReward, cosmetics, rank (GTMRank), money, bank, killCounter, permits," +
                    " jobMode, backpack, kitExpiries, houses, premiumHouses, gang, jail, ammo, vehicles, inventory (includes echest), eventTag"));
            return true;
        }
        if (args.length > 3) {
            return true;
        }
        String name = args[0];
        Player target = Bukkit.getPlayer(name);
        UUID uuid = null;
        if (target != null) {
            name = target.getName();
            uuid = target.getUniqueId();
            target.kickPlayer("You are being reset by an admin.");
        }
        String server = Core.name();
        if (args.length == 3) {
            if (Core.getServerManager().getServer(args[2]) != null) {
                server = args[2];
            }
        }

        String finalName = name;
        UUID finalUniqueId = uuid;
        String finalServer = server;

        if ("all".equalsIgnoreCase(args[1])) {
            ServerUtil.runTaskAsync(() -> {
                UUID value = UserDAO.getUuidByName(finalName);

                try {
                    File file = new File(Bukkit.getWorldContainer() + "/" + Bukkit.getWorlds().get(0).getName() + "/playerdata/" + value.toString());
                    if (file.exists()) file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                BaseDatabase.runCustomQuery("delete from user_tag where uuid=UNHEX('" + value.toString().replaceAll("-", "") + "');");

                BaseDatabase.runCustomQuery("update users set tokens=0, bucks=0, votes=0, voteStreak=0, lastVoteStreak=0, dailyStreak=0, lastDailyReward=0, lastDonorReward=0 where uuid='" + value.toString() + "';");
//                BaseDatabase.runCustomQuery("delete from cosmetics where uuid='" + value.toString() + "';"); NOT NEEDED.
                BaseDatabase.runCustomQuery("delete from " + finalServer + " where uuid=UNHEX('" + value.toString().replaceAll("-", "") + "');");
//                BaseDatabase.runCustomQuery("delete from " + finalServer + "_gangs where leaderName='" + finalName + "';"); TODO DELETE GANG
//                BaseDatabase.runCustomQuery("delete from " + finalServer + "_houses where uuid='" + finalName + "';"); TODO DELETE HOUSE
                BaseDatabase.runCustomQuery("update " + finalServer + " set backpackContents=NULL where uuid=UNHEX('" + value.toString().replaceAll("-", "") + "');");

                ServerUtil.runTask(() -> {
                    for (PremiumHouse house : Houses.getHousesManager().getPremiumHouses()) {
                        if (house.getOwner() != null && house.getOwnerName().equalsIgnoreCase(finalName)) {
                            house.removeOwner(true);
                        }
                    }

                    for (Gang gang : GangManager.getInstance().getGangs()) {
                        if (gang.getOwnerName() != null && gang.getOwnerName().equalsIgnoreCase(finalName)) {
//                            gang.disbandConfirm(); TODO FORCE DELETE.
                        }
                    }

                    s.sendMessage(Utils.f("&7You fully reset player &a" + finalName + "&7!"));
                });
            });
            return true;
        }

        switch (args[1].toLowerCase()) {
            case "eventtag": {
                ServerUtil.runTaskAsync(() -> {
                    UUID value = UserDAO.getUuidByName(finalName);
                    BaseDatabase.runCustomQuery("delete from user_tag where uuid=UNHEX('" + value.toString().replaceAll("-", "") + "');");
                });
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            }
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
                });
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "tokens":
//                Core.sql.updateAsyncLater("update users set tokens=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update users set tokens=0 where lastname='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "bucks":
//                Core.sql.updateAsyncLater("update users set bucks=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update users set bucks=0 where lastname='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "votes":
//                Core.sql.updateAsyncLater("update users set votes=0, voteStreak=0, lastVoteStreak=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update users set votes=0, voteStreak=0, lastVoteStreak=0 where lastname='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "dailystreak":
//                Core.sql.updateAsyncLater("update users set dailyStreak=0, lastDailyReward=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update users set dailyStreak=0, lastDailyReward=0 where lastname='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "lastdonorreward":
//                Core.sql.updateAsyncLater("update users set lastDonorReward=0 where lastname='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update users set lastDonorReward=0 where lastname='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "cosmetics":
//                Core.sql.updateAsyncLater("delete from cosmetics where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("delete from cosmetics where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "rank":
//                Core.sql.updateAsyncLater("update " + server + " set rank='HOBO' where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set rank='HOBO' where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "money":
//                Core.sql.updateAsyncLater("update " + server + " set money=0 where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set money=0 where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "bank":
//                Core.sql.updateAsyncLater("update " + server + " set bank=0 where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set bank=0 where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "killCounter":
//                Core.sql.updateAsyncLater("update " + server + " set killCounter=0 where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set killCounter=0 where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "permits":
//                Core.sql.updateAsyncLater("update " + server + " set permits=0 where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set permits=0 where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "jobmode":
//                Core.sql.updateAsyncLater("update " + server + " set jobMode='CRIMINAL', lastJobMode=0 where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set jobMode='CRIMINAL', lastJobMode=0 where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "backpack":
//                Core.sql.updateAsyncLater("update " + server + " set backpackContents=NULL where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set backpackContents=NULL where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "kitexpiries":
//                Core.sql.updateAsyncLater("update " + server + " set kitExpiries=null where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set kitExpiries=null where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "houses":
//                Core.sql.updateAsyncLater("delete from " + server + " where name='" + name + "';");
//                Core.sql.updateAsyncLater("delete from " + server + "_houses where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    BaseDatabase.runCustomQuery("delete from " + finalServer + " where name='" + finalName + "';");
                    BaseDatabase.runCustomQuery("delete from " + finalServer + "_houses where name='" + finalName + "';");

                    UUID id = finalUniqueId == null ? UserDAO.getUuidByName(finalName) : finalUniqueId;
                    if(id != null) BaseDatabase.runCustomQuery("delete from " + finalServer + "_houses_chests where uuid='" + id + "';");
                });
//                if (uuid == null) {
//                    String finalName = name;
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
//                        }
//                    }.runTaskAsynchronously(GTM.getInstance());
//                } else {
//                    Core.sql.updateAsyncLater("delete from " + server + "_houses_chests where uuid='" + uuid + "';");
//                }
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "premiumhouses":
                for (PremiumHouse house : Houses.getHousesManager().getPremiumHouses())
                    if (house.getOwner() != null && house.getOwnerName().equalsIgnoreCase(name))
                        house.removeOwner(true);
                return true;
            case "gang":
//                Core.sql.updateAsyncLater("update " + server + " set gang=null, gangRank='member' where name='" + name + "';");
//                Core.sql.updateAsyncLater("delete from " + server + "_gangs where leaderName='" + name + "';");
                ServerUtil.runTaskAsync(() -> {
                    BaseDatabase.runCustomQuery("update " + finalServer + " set gang=null, gangRank='member' where name='" + finalName + "';");
                    BaseDatabase.runCustomQuery("delete from " + finalServer + "_gangs where leaderName='" + finalName + "';");
                });
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "jail":
//                Core.sql.updateAsyncLater("update " + server + " set jailTimer=-1, jailCop=NULL, jailCopName=NULL where name='" + name + "';");
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set jailTimer=-1, jailCop=NULL, jailCopName=NULL where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            case "ammo": {
                String st = "";
                for (AmmoType type : AmmoType.values())
                    st += type.getGameItemName() + "=0, ";
                if (st.endsWith(", "))
                    st = st.substring(0, st.length() - 2);
//                Core.sql.updateAsyncLater("update " + server + " set " + st + " where name='" + name + "';");
                String finalSt = st;
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set " + finalSt + " where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            }
            case "vehicles":
                String st = "personalVehicle=NULL, ";
                for (VehicleProperties v : GTM.getWastedVehicles().getBabies().getVehicleProperties())
                    st += '`' + v.getIdentifier().toLowerCase() + "`=0, `" + v.getIdentifier().toLowerCase() + ":info`=NULL, ";
//                Core.sql.updateAsyncLater("update " + server + " set " + st + " where name='" + name + "';");
                String finalSt = st;
                ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + finalServer + " set " + finalSt + " where name='" + finalName + "';"));
                s.sendMessage(Utils.f("&7You reset player &a" + name + "&7 for category &a" + args[1] + "&7!"));
                return true;
            default:
                s.sendMessage(Utils.f("&c/reset&7 [player] [category/all] <server>"));
                s.sendMessage(Utils.f("&7Categories: tokens, bucks, votes, dailyStreak," +
                        " lastDonorReward, rank (GTMRank), money, bank, killCounter, permits, jobMode, backpack, kitExpiries, houses, gang, jail, ammo, vehicles"));
                return true;
        }

    }
}
