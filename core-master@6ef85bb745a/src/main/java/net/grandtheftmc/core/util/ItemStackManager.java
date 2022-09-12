package net.grandtheftmc.core.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.events.ItemStackEvent;

/**
 * Created by Luke Bingham on 06/08/2017.
 */
public class ItemStackManager implements Listener {

    public static final HashMap<Material, Integer> STACKABLES;

    static {
        STACKABLES = Maps.newHashMap();
        new ItemStackManager();
    }

    public ItemStackManager() {
        Bukkit.getPluginManager().registerEvents(this, Core.getInstance());
    }

    @EventHandler //TODO
    protected final void onInventoryClick(InventoryClickEvent event) {
    	
        if(event.isCancelled()) return;
        if(event.getSlotType() == null) return;
        boolean cursorStack = false;
        if(event.getSlotType() == InventoryType.SlotType.RESULT) {
            if(event.getRawSlot() == 0) {
                return;
            }
        }

        ItemStack cursor = event.getCursor(), clicked = event.getCurrentItem();
        if(event.getClick() == ClickType.SHIFT_RIGHT || event.getClick() == ClickType.SHIFT_LEFT) {
            ItemStackEvent stackEvent = new ItemStackEvent(clicked);
            Bukkit.getPluginManager().callEvent(stackEvent);

            if(stackEvent.isClickOnly()) {
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
                return;
            }
        }

        // if one of the cursors or clicked is null, allow
        if(cursor == null || clicked == null) return;
        
        // if they are not the same type, allow
        if(cursor.getType() != clicked.getType()) return;
        //if(!STACKABLES.containsKey(cursor.getType())) return;
        if(cursor.getDurability() != clicked.getDurability()) return;
        
        ((Player) event.getWhoClicked()).updateInventory();
        if((clicked.getAmount() >= 64 || cursor.getAmount() >= 64) || (clicked.getAmount() + cursor.getAmount() > 64)) {
            event.setCancelled(true);
            return;
        }

        if(!cursor.hasItemMeta() && !clicked.hasItemMeta()) {
            ItemStackEvent stackEvent = new ItemStackEvent(clicked);
            Bukkit.getPluginManager().callEvent(stackEvent);

            if(!stackEvent.isCancelled()) {
                event.setCancelled(true);
                if(!cursorStack) {
                    clicked.setAmount(clicked.getAmount() + cursor.getAmount());
                    event.setCursor(new ItemStack(Material.AIR));
                }
                ((Player) event.getWhoClicked()).updateInventory();
            } else event.setCancelled(true);
            return;
        }

        if(cursor.getItemMeta() == null || cursor.getItemMeta().getDisplayName() == null) return;
        if(!cursor.getItemMeta().getDisplayName().equals(clicked.getItemMeta().getDisplayName()))
            return;

        ItemStackEvent stackEvent = new ItemStackEvent(clicked);
        Bukkit.getPluginManager().callEvent(stackEvent);
        
        if(!stackEvent.isCancelled()) {
            event.setCancelled(true);
            if(!cursorStack) {
                clicked.setAmount(clicked.getAmount() + cursor.getAmount());
                event.setCursor(new ItemStack(Material.AIR));
            }
            ((Player) event.getWhoClicked()).updateInventory();
        } else event.setCancelled(true);
    }
}
