package net.grandtheftmc.Bungee.listeners;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Objects;

public class Kick implements Listener {

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ProxyServer proxy = Bungee.getInstance().getProxy();
        ServerInfo kickedFrom;

        if (event.getPlayer().getServer() != null) {
            kickedFrom = event.getPlayer().getServer().getInfo();
        }
        else if (proxy.getReconnectHandler() != null) {
            kickedFrom = proxy.getReconnectHandler().getServer(event.getPlayer());
        }
        else {
            kickedFrom = AbstractReconnectHandler.getForcedHost(event.getPlayer().getPendingConnection());
            if (kickedFrom == null)
                kickedFrom = proxy.getServerInfo(event.getPlayer().getPendingConnection().getListener().getServerPriority().get(0));
        }

        ServerInfo kickTo = Utils.getRandomHub();
        if (Objects.equals(kickedFrom, kickTo)) return;
        event.setCancelled(true);
        event.setCancelServer(kickTo);
        player.sendMessage(event.getKickReasonComponent());
    }
}
