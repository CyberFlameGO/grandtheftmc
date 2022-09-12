package net.grandtheftmc.Creative.listeners;

import net.grandtheftmc.Creative.Creative;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Created by Liam on 22/07/2017.
 */
public class Leave implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeaveMonitor(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Creative.getUserManager().unloadUser(uuid);
    }
}
