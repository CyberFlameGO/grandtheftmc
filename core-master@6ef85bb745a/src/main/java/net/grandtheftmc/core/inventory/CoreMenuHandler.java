package net.grandtheftmc.core.inventory;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public class CoreMenuHandler implements Listener {//TODO, Implement Component

    public CoreMenuHandler(Core plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event){
        if(event.getInventory().getHolder() == null) return;
        if(!(event.getInventory().getHolder() instanceof CoreMenu)) return;
        if(!(event.getWhoClicked() instanceof Player)) return;

        CoreMenu menu = (CoreMenu) event.getInventory().getHolder();

        boolean blocked = false;
        for (int slot : event.getRawSlots()) {
            if (menu.isSlotBlocked(slot)) {
                blocked = true;
                break;
            }
        }

        menu.onInteract(menu);

        event.setCancelled(blocked);
    }

    @EventHandler
    protected void onInventoryClick(InventoryClickEvent event) {
        if(event.getInventory().getHolder() == null) return;
        if(!(event.getInventory().getHolder() instanceof CoreMenu)) return;
        if(!(event.getWhoClicked() instanceof Player)) return;

        CoreMenu menu = (CoreMenu) event.getInventory().getHolder();

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        if(clicked == null && menu.hasFlag(CoreMenuFlag.CLOSE_ON_NULL_CLICK)) {
            player.closeInventory();
            return;
        }

        menu.onInteract(menu);

        if(menu.isSelfHandle()) {
            menu.selfHandle(event);
            return;
        }
        menu.onClick(event);
        MenuItem item = menu.getMenuItem(event.getRawSlot());
        if(item == null) return;

        if(!item.isAllowingPickup())
            event.setCancelled(true);
        if(item instanceof ClickableItem) {
            ((ClickableItem) item).getClickAction().onClick(player, event.getClick());
        }
    }

    @EventHandler
    protected void onInventoryClose(InventoryCloseEvent event) {
        if(event.getInventory().getHolder() == null)
            return;
        if(!(event.getInventory().getHolder() instanceof CoreMenu))
            return;
        if(!(event.getPlayer() instanceof Player))
            return;
        CoreMenu menu = (CoreMenu) event.getInventory().getHolder();
        menu.onClose(event);
    }
}
