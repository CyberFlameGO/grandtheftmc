package net.grandtheftmc.hub.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlaceEvent(BlockPlaceEvent event) {
        if (!event.getPlayer().isOp()) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }
}
