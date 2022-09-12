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
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;

public class ServerCommand extends Command {

    private final JedisManager jedisManager;
    private final String command;

    public ServerCommand(String command, JedisManager jedisManager) {
        super(command);
        this.command = command;
        this.jedisManager = jedisManager;
    }

    /**
     * Connects the player to the specific Bungee server.
     * @param s Who executes the command.
     * @param args The desired server ID to connect to.
     */
    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) s;
        ServerInfo info = ProxyServer.getInstance().getServerInfo(Bungee.getSettings().getServers().get(this.command));
        if (info == null) {
            player.sendMessage(Utils.ft("&cThat server does not exist!"));
            return;
        }

        if(player.getServer().getInfo().getName().equals(info.getName())) {
            player.sendMessage(Utils.ft("&cYou're already connected to this server!"));
            return;
        }

        int id = -1;
        ServerType type = ServerType.OPERATOR;
        if(this.command.toLowerCase().startsWith("hub")) {
            id = Integer.parseInt(this.command.toLowerCase().split("hub")[1]);
            type = ServerType.HUB;
        }

        if(this.command.toLowerCase().startsWith("gtm")) {
            id = Integer.parseInt(this.command.toLowerCase().split("gtm")[1]);
            type = ServerType.GTM;
        }

        if(this.command.toLowerCase().startsWith("vice")) {
            id = Integer.parseInt(this.command.toLowerCase().split("vice")[1]);
            type = ServerType.VICE;
        }

        if(this.command.toLowerCase().startsWith("creative")) {
            id = Integer.parseInt(this.command.toLowerCase().split("creative")[1]);
            type = ServerType.CREATIVE;
        }

        if(type == ServerType.OPERATOR || id == -1) return;

//        player.connect(info);
        Optional<User> user = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        UserRank rank = user.map(User::getUserRank).orElse(UserRank.DEFAULT);
        this.jedisManager.getModule(JedisChannel.SERVER_QUEUE).sendMessage(
                new ServerQueueMessage(player.getUniqueId(), rank.name(), new ServerTypeId(type, id)),
                new ServerTypeId(ServerType.OPERATOR, -1)
        );
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return ProxyServer.getInstance().getServers().keySet();
    }

}
