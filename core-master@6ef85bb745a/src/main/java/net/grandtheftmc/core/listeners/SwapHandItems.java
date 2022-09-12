package net.grandtheftmc.core.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.events.PlayerFActionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class SwapHandItems implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        PlayerFActionEvent playerFActionEvent = new PlayerFActionEvent(player);
        Core.getInstance().getServer().getPluginManager().callEvent(playerFActionEvent);
        if (playerFActionEvent.isCancelled()) event.setCancelled(true);
    }
}
