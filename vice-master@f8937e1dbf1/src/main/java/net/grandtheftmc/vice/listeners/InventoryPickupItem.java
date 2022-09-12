package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.vice.Vice;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Timothy Lampen on 2017-08-07.
 */
public class InventoryPickupItem implements Listener {

    @EventHandler
    public void onPickup(InventoryPickupItemEvent event){
        ItemStack item = event.getItem().getItemStack();
        HashMap<ItemStack, ItemStack> replacedVanillaItems = Vice.getItemManager().getReplacedVanilla();
        if(replacedVanillaItems.keySet().stream().anyMatch(value -> value.isSimilar(item))) {
            Optional<Map.Entry<ItemStack, ItemStack>> optItemStack = replacedVanillaItems.entrySet().stream().filter(map -> map.getKey().isSimilar(item)).findFirst();
            if (optItemStack.isPresent()) {
                event.setCancelled(true);
                event.getItem().remove();
                ItemStack newItem = optItemStack.get().getValue();
                newItem.setAmount(item.getAmount());
                Item newI = event.getItem().getWorld().dropItem(event.getItem().getLocation(), newItem);
                newI.setPickupDelay(0);
            }
        }
    }
}
