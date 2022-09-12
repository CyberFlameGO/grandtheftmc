package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.users.UserRank;
import net.grandtheftmc.ServerType;
import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.jedis.JedisChannel;
import net.grandtheftmc.jedis.JedisManager;
import net.grandtheftmc.jedis.message.ServerQueueMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;

public class HubCommand extends Command {

    private final JedisManager jedisManager;
    private final String command;

    public HubCommand(JedisManager jedisManager, String command) {
        super(command, null);
        this.jedisManager = jedisManager;
        this.command = command;
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) s;
//        player.connect(Utils.getRandomHub());
        ServerInfo info = null;
        ServerType type = ServerType.GLOBAL;

        int id = -1;
        if(command.toLowerCase().startsWith("hub")) {
            type = ServerType.HUB;
            info = Utils.getRandomHub();
            id = Integer.parseInt(info.getName().toLowerCase().split("hub")[1]);
        }

        if(command.toLowerCase().startsWith("vice")) {
            type = ServerType.VICE;
            info = Utils.getRandomServer(command);
            id = Integer.parseInt(info.getName().toLowerCase().split("vice")[1]);
        }
        if(command.toLowerCase().startsWith("gtm") || command.toLowerCase().startsWith("gta")) {
            type = ServerType.GTM;
            info = Utils.getRandomServer(command);
            id = Integer.parseInt(info.getName().toLowerCase().split("gtm")[1]);
        }

        if(id == -1 || info == null || type == ServerType.GLOBAL) {
            s.sendMessage(Utils.ft("&cServer couldn't be found."));
            return;
        }

        Optional<User> user = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        UserRank rank = user.map(User::getUserRank).orElse(UserRank.DEFAULT);
        this.jedisManager.getModule(JedisChannel.SERVER_QUEUE).sendMessage(
                new ServerQueueMessage(player.getUniqueId(), rank.name(), new ServerTypeId(type, id)),
                new ServerTypeId(ServerType.OPERATOR, -1)
        );
    }

}