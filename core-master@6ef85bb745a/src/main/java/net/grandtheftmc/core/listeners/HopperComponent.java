package net.grandtheftmc.core.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Created by Luke Bingham on 26/08/2017.
 */
public class HopperComponent implements Component<HopperComponent, Core> {

    @EventHandler
    protected final void onHopperPickup(InventoryPickupItemEvent event) {
        if(event.getInventory() == null && event.getInventory().getType() != InventoryType.HOPPER) return;
        if(event.getItem() == null && (event.getItem().getItemStack().getType() != Material.DIAMOND_SWORD && event.getItem().getItemStack().getType() != Material.FLINT_AND_STEEL))
            return;

        Item item = event.getItem();

        if(event.getItem().getItemStack().getType() == Material.DIAMOND_SWORD) {
            if(item.getItemStack().getDurability() <= 751 || item.getItemStack().getDurability() >= 800) {
                event.setCancelled(true);
                return;
            }
        }

        if(event.getItem().getItemStack().getType() == Material.FLINT_AND_STEEL) {
            event.setCancelled(true);
            return;
        }
    }
}
