package net.grandtheftmc.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if(inv != null && (inv.getType() == InventoryType.CHEST ||
                inv.getType() == InventoryType.ENDER_CHEST ||
                inv.getType().name().equals("SHULKER_BOX") ||
                inv.getType() == InventoryType.DISPENSER ||
                inv.getType() == InventoryType.DROPPER ||
                inv.getType() == InventoryType.HOPPER)) {
            ItemStack selected = e.isShiftClick() ? e.getCurrentItem() : e.getCursor();
            if(selected != null && selected.getType().toString().contains("SHULKER_BOX")){
                e.setCancelled(true);
                return;
            }
        }
       /* if (!Core.getSettings().useEditMode()) {
            return;
        }
        User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (u.hasEditMode()) {
            if (e.isCancelled()) {
                e.setCancelled(false);
            }
        }*/
    }
}
