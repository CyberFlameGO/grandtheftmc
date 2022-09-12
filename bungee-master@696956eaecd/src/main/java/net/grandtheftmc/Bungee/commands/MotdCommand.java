package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.Map;

public class MotdCommand extends Command {

    public MotdCommand() {
        super("motd", "motd", "gmotd", "bungeemotd");
    }

    /*
        Redis procedure:
        ----------------

        Reload:
        -------
        1) Send a pub sub message to all instances to force a reload of the MOTD configurations.

        Set:
        ----
        1) Send a pub sub message to all instances to set a new MOTD message.
     */

    @Override
    public void execute(CommandSender s, String[] args) {

        if (args.length == 0) {
            s.sendMessage(Utils.ft("&7/motd reload"));
            s.sendMessage(Utils.ft("&7/motd set <msg>"));
            return;
        }

        switch (args[0]) {

            case "reload":
                Map<String, Object> map = new HashMap<>();
                map.put("reload", true);
                map.put("sender", (s instanceof ProxiedPlayer) ? ((ProxiedPlayer) s).getName() : "CONSOLE");

                String ser = Bungee.getRedisManager().serialize(DataType.MOTD, map);
                //Send this serialised object to other redis servers for handling...
                //The player/console is notified on the listener end..
                Bungee.getRedisManager().sendMessage(ser);
                return;

            case "set":
                String msg = "";
                for (int i = 1; i < args.length; i++)
                    msg += args[i] + ' ';
                if (msg.endsWith(" "))
                    msg = msg.substring(0, msg.length() - 1);

                map = new HashMap<>();
                map.put("motd", msg);
                map.put("sender", (s instanceof ProxiedPlayer) ? ((ProxiedPlayer) s).getName() : "CONSOLE");

                ser = Bungee.getRedisManager().serialize(DataType.MOTD, map);
                //Send this serialised object to other redis servers for handling...
                //The player/console is notified on the listener end..
                Bungee.getRedisManager().sendMessage(ser);
                return;

            default:
                break;
        }

    }
}