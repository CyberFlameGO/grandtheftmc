package net.grandtheftmc.hub.listeners;

import net.grandtheftmc.core.menus.MenuManager;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.inventory.InventoryType;

public class PortalEnter implements Listener {

    @EventHandler
    public void playerPortalEvent(EntityPortalEnterEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        if (player.getOpenInventory() != null && player.getOpenInventory().getType() == InventoryType.CHEST) return;
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1F);
        player.setVelocity(player.getLocation().getDirection().setY(2).multiply(-1));
        MenuManager.openMenu(player, "serverwarper");
    }
}
