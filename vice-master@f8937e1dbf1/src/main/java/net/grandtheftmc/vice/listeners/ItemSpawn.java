package net.grandtheftmc.vice.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

public class ItemSpawn implements Listener {
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        Item item = e.getEntity();
        ItemStack stack = item.getItemStack();

        if (stack != null) {
            if (stack.hasItemMeta()) {
                switch (ChatColor.stripColor(stack.getItemMeta().getDisplayName())) {
                    case "Backpack":
                    case "Ammo Pouch":
                    case "Phone": {
                        item.remove();

                        break;
                    }
                }
            }
        }
    }
}