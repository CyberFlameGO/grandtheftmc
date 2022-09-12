package net.grandtheftmc.gtm.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public class Craft implements Listener {

    @EventHandler
    public void craftItem(CraftItemEvent event) {
        if (event.getWhoClicked().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getWhoClicked();

        if (!player.isOp()) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            player.getInventory().remove(event.getRecipe().getResult());
        }
    }

    @EventHandler
    public void prepareItemCraft(PrepareItemCraftEvent event) {
    }
}
