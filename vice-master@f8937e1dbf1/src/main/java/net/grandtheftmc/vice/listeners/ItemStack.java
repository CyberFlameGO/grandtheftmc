package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.events.ItemStackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Timothy Lampen on 3/10/2018.
 */
public class ItemStack implements Listener {

    @EventHandler
    public void onStack(ItemStackEvent event) {
        org.bukkit.inventory.ItemStack is = event.getItemStack();
        if(is!=null && is.getType()== Material.PRISMARINE_SHARD)
            event.setCancelled(true);
    }
}
