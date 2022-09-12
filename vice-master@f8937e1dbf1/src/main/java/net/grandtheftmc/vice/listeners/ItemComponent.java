package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.GameItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemComponent implements Component<ItemComponent, Vice> {

    public ItemComponent() {
        Bukkit.getPluginManager().registerEvents(this, Vice.getInstance());
    }

    @EventHandler
    protected final void onItemRaname(InventoryClickEvent event) {
        if (event.getInventory() == null) return;
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        if (event.getRawSlot() != event.getView().convertSlot(event.getRawSlot())) return;
        if (event.getRawSlot() != 2) return;

        if (event.getInventory().getItem(0) == null) return;
        if (event.getInventory().getItem(0).getType() == Material.AIR) return;

        if (event.getInventory().getItem(2) == null) return;
        if (event.getInventory().getItem(2).getType() == Material.AIR) return;

        ItemStack itemStack = event.getInventory().getItem(0);
        for (GameItem gameItem : Vice.getItemManager().getItems()) {
            if (gameItem.getItem().isSimilar(itemStack)) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
