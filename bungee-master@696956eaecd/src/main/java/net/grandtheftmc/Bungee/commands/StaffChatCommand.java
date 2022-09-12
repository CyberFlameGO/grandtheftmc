package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.utils.TabComplete;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.*;
import java.util.stream.Collectors;

public class StaffChatCommand extends Command {

    public StaffChatCommand() {
        super("staffchat", "staffchat.use", "sc", "staffc", "schat", "adminchat", "adminc", "ac", "achat");
    }

    /*
        Redis procedure:
        ----------------
        1) Staff chat messages should simply be forwarded to all redis instances.
     */

    @Override
    public void execute(CommandSender s, String[] args) {
        //Allow to chat in staff chat via console
        if (!(s instanceof ProxiedPlayer)) {
            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < args.length; i++)
                msg.append(i > 0 ? " " : "").append(args[i]);
            //broadcast to redis
            Map<String, Object> map = new HashMap<>();
            map.put("sender", "CONSOLE");
            map.put("message", msg.toString());

            String ser = Bungee.getRedisManager().serialize(DataType.STAFFCHAT, map);
            //Send this serialised object to other redis servers for handling...
            Bungee.getRedisManager().sendMessage(ser);
            return;
        }


        ProxiedPlayer player = (ProxiedPlayer) s;
        //This list only contains staff anyway.
        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        userOptional.ifPresent(user -> {
            if (args.length == 0) {
                user.toggleStaffChat();
                s.sendMessage(Lang.STAFF.ft("&7You turned " + (user.getStaffChat() ? "&a&lon" : "&c&loff") + "&7 staff chat!"));
                return;
            }

            if ("on".equalsIgnoreCase(args[0])) {
                user.setStaffChat(true);
                s.sendMessage(Lang.STAFF.ft("&7You turned &a&lon&7 staff chat!"));
                return;
            }

            if ("off".equalsIgnoreCase(args[0])) {
                user.setStaffChat(false);
                s.sendMessage(Lang.STAFF.ft("&7You turned &c&loff&7 staff chat!"));
                return;
            }

            String msg = "";
            for (int i = 0; i < args.length; i++)
                msg = (i > 0 ? " " : "") + args[i];

            //broadcast to redis
            Map<String, Object> map = new HashMap<>();
            map.put("sender", player.getName());
            map.put("message", msg);

            String ser = Bungee.getRedisManager().serialize(DataType.STAFFCHAT, map);
            //Send this serialised object to other redis servers for handling...
            Bungee.getRedisManager().sendMessage(ser);
        });
    }


    //Autocompleting playing names accross the redis network.
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return TabComplete.onTabComplete(sender, args);
    }
}
