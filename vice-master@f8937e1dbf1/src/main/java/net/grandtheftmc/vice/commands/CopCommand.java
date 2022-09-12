package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.CopRank;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.ViceUserDAO;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Liam on 3/07/2017.
 */
public class CopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        if (args.length == 0) {
            s.sendMessage(Utils.f("&3/cop&7 set <player> <copRank> - Set the player's cop rank"));
            s.sendMessage(Utils.f("&3/cop&7 promote <player> - Promotes the player to a higher Cop Rank"));
            s.sendMessage(Utils.f("&3/cop&7 resign - Resigns your current position"));
            s.sendMessage(Utils.f("&3/cop&7 list - Lists all avaliable online and offline cops"));
            s.sendMessage(Utils.f("&3/cop&7 fire <player> - Fire the player from being a Cop"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list": {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        HashMap<String, CopRank> onlineCops = new HashMap<>();
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                            if(viceUser.getCopRank()!=null) {
                                onlineCops.put(player.getName(), viceUser.getCopRank());
                            }
                        }
                        HashMap<String, CopRank> offlineCops = new HashMap<>();
//                            ResultSet rs = Core.sql.prepareStatement("SELECT * from " + Core.name() + " WHERE copRank IS NOT NULL;").executeQuery();
//                            while (rs.next()) {
//                                String name = rs.getString("name");
//                                if(onlineCops.containsKey(name))
//                                    continue;
//                                CopRank rank  = CopRank.getRankOrNull(rs.getString("copRank"));
//                                offlineCops.put(name, rank);
//                            }

                        ViceUserDAO.getCops(onlineCops, offlineCops);

                        List<Map.Entry<String,CopRank>> sortedOnline = sortMapByCopRankValueDescending(onlineCops);
                        List<Map.Entry<String,CopRank>> sortedOffline = sortMapByCopRankValueDescending(offlineCops);
                        s.sendMessage(Lang.COP.f("&a&lONLINE COPS:"));
                        sortedOnline.forEach(entry -> {s.sendMessage(Utils.f(ChatColor.GREEN + entry.getKey() + " &7: &3" + entry.getValue()));});
                        s.sendMessage(Lang.COP.f("&4&lOFFLINE COPS:"));
                        sortedOffline.forEach(entry -> {s.sendMessage(Utils.f(ChatColor.RED + entry.getKey() + " &7: &3" + entry.getValue()));});
                    }
                }.runTaskAsynchronously(Vice.getInstance());
                return true;
            }
            case "resign": {
                if(!(s instanceof Player)) {
                    s.sendMessage(Lang.COP.f("&7You have to be a player to execute this command!"));
                    return false;
                }
                Player player = (Player)s;
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                if(viceUser.getCopRank()!=CopRank.WARDEN) {
                    viceUser.setCopRank(null, player, user);
                    s.sendMessage(Lang.COP.f("&7You have resigned as a cop. Thank you for your support with our police force."));
                    ViceUtils.updateBoard(player, viceUser);
                    return true;
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int counter = ViceUserDAO.countCops(CopRank.WARDEN);

                        if(counter>=2) {
                            viceUser.setCopRank(null, player, user);
                            ViceUtils.updateBoard(player, viceUser);
                            s.sendMessage(Lang.COP.f("&7You have resigned as a warden. Thank you for your support with our police force."));
                        }
                        else {
                            s.sendMessage(Lang.COP.f("&7You cannot resign as there are no other wardens to take your place!"));
                        }
                    }
                }.runTaskAsynchronously(Vice.getInstance());
                return true;
            }
            case "set": {
                if (!senderHasPerms(s)) {
                    s.sendMessage(Lang.NOPERM.s());
                    return false;
                }
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/cop set <player> <copRank>"));
                    return false;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Lang.COPS.f("&7That player is not online!"));
                    return false;
                }
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                CopRank copRank = CopRank.getRankOrNull(args[2]);
                if (copRank == null) {
                    String msg = Lang.COPS + "&7There is no Cop Rank with the name &3" + args[2] + "&7! Valid ranks: ";
                    for (CopRank r : CopRank.values())
                        msg = msg + "&3" + r.getColoredNameBold() + "&7, ";
                    if (msg.endsWith("&7, "))
                        msg = msg.substring(0, msg.length() - 4);
                    msg += "&7.";
                    s.sendMessage(Utils.f(msg));
                    return true;
                }
                ViceUtils.updateBoard(player, viceUser);
                viceUser.setCopRank(copRank, player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
                s.sendMessage(Lang.COPS.f("&7You set &3&l" + player.getName() + "&7 to " + copRank.getColoredNameBold() + "&7!"));
                player.sendMessage(Lang.COPS.f("&7You have been set to " + copRank.getColoredNameBold() + "&7 by &3&l" + s.getName() + "&7!"));
                return true;
            }
            case "promote": {
                if (!senderHasPerms(s)) {
                    s.sendMessage(Lang.NOPERM.s());
                    return false;
                }
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/cop promote <player>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Lang.COPS.f("&7That player is not online!"));
                    return true;
                }
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                CopRank copRank = viceUser.isCop() ? viceUser.getCopRank().getNext() : CopRank.COP;
                viceUser.setCopRank(copRank, player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
                ViceUtils.updateBoard(player, viceUser);
                s.sendMessage(Lang.COPS.f("&7You promoted &3&l" + player.getName() + "&7 to " + copRank.getColoredNameBold() + "&7!"));
                player.sendMessage(Lang.COPS.f("&7You have been promoted to " + copRank.getColoredNameBold() + "&7 by &3&l" + s.getName() + "&7!"));
                return true;
            }
            case "fire":
                if (!senderHasPerms(s)) {
                    s.sendMessage(Lang.NOPERM.s());
                    return true;
                }
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/cop fire <player>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
                    s.sendMessage(Lang.COPS.f("&7That player is not online!"));
                    return true;
                }
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                viceUser.setCopRank(null, player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
                ViceUtils.updateBoard(player, viceUser);
                s.sendMessage(Lang.COPS.f("&7You fired &3&l" + player.getName() + "&7 from the police force!"));
                player.sendMessage(Lang.COPS.f("&7You have been fired from the police force by &3&l" + s.getName() + "&7!"));
                return true;
        }
        return true;
    }

    private boolean senderHasPerms(CommandSender sender) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            return user.isAdmin() && viceUser.getCopRank() == CopRank.WARDEN;
        }
        return true;
    }

    private List<Map.Entry<String,CopRank>> sortMapByCopRankValueDescending(Map<String,CopRank> map) {
        List<Map.Entry<String,CopRank>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        return sortedEntries;
    }
}
