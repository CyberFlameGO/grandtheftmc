package net.grandtheftmc.core.servers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.grandtheftmc.core.Core;

public class ServerPingListener implements Listener {

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        ServerManager sm = Core.getServerManager();
        e.setMotd("map=" + sm.getMap() + ",gameState=" + sm.getGameState() + ",round=" + sm.getRound());
    }
}
