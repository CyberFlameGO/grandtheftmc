package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.database.BaseDatabase;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.users.UserRank;
import net.grandtheftmc.Bungee.utils.TabComplete;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermsCommand extends Command {

    public PermsCommand() {
        super("gperms", "gperms", "gpermissions", "globalperms", "globalpermissions");
    }

    /*
        Redis procedure:
        ----------------
        - Send out a request to reload all configurations
        - Checking userank, direct database lookup
        - Perms update <user>
     */

    @Override
    public void execute(CommandSender s, String[] args) {
        if (args.length == 0) {
            s.sendMessage(Utils.ft("&7/gperms reload"));
            s.sendMessage(Utils.ft("&7/gperms check <username>"));
            s.sendMessage(Utils.ft("&7/gperms update <username>"));
            return;
        }

        String sender = s instanceof ProxiedPlayer ? s.getName() : "CONSOLE";

        switch (args[0].toLowerCase()) {
            case "reload":
                Map<String, Object> map = new HashMap<>();
                map.put("reload", true);
                map.put("sender", sender);
                String ser = Bungee.getRedisManager().serialize(DataType.PERMS, map);
                Bungee.getRedisManager().sendMessage(ser);
                return;

            case "check":
                if (args.length != 2) {
                    s.sendMessage(Utils.ft("&7/gperms check <username>"));
                    return;
                }
                Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
                    try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                         try (PreparedStatement statement = connection.prepareStatement("SELECT lastname,userrank FROM users WHERE lastname=?;")) {
                             statement.setString(1, args[1]);
                             try (ResultSet result = statement.executeQuery()) {
                                 if (result.isBeforeFirst()) {
                                     if (result.isBeforeFirst()) {
                                         result.next();
                                         UserRank ur = UserRank.getUserRank(result.getString("userrank"));
                                         s.sendMessage(Utils.ft("&7User " + args[1] + " rank: " + ur.getColoredName()));
                                     } else {
                                         s.sendMessage(Utils.ft("&7The user " + args[1] + " does not exist."));
                                     }
                                 }
                             }
                         }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                return;

            case "update":
                if (args.length != 2) {
                    s.sendMessage(Utils.ft("&7/gperms update <username>"));
                    return;
                }

                UUID u = Bungee.getRedisManager().getRedisAPI().getUuidFromName(args[1]);
                if (u == null || !Bungee.getRedisManager().getRedisAPI().isPlayerOnline(u)) {
                    Utils.msg(s, "&7That player is not online!");
                    return;
                }

                map = new HashMap<>();
                map.put("sender", sender);
                map.put("target", args[1]);
                ser = Bungee.getRedisManager().serialize(DataType.PERMS, map);
                Bungee.getRedisManager().sendMessage(ser);
                return;

            default:
                s.sendMessage(Utils.ft("&7/gperms reload"));
                s.sendMessage(Utils.ft("&7/gperms check <username>"));
                s.sendMessage(Utils.ft("&7/gperms update <username>"));
                return;
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return TabComplete.onTabComplete(sender, args);
    }

}