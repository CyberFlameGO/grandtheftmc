package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.gtm.GTMUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class Drop implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack().clone();
        if(item==null)
            return;
        switch (item.getType()) {
        case CHEST:
        case WATCH:
        case COMPASS:
            e.getItemDrop().remove();
            item.setAmount(1);
            GTMUtils.giveGameItems(e.getPlayer());
            return;
        }

        if(item.getType().toString().contains("LEATHER_")) {
            if(ArmorEquip.isCustomColor(((LeatherArmorMeta)item.getItemMeta()).getColor())) {
                e.getItemDrop().remove();
                e.getPlayer().updateInventory();
            }
        }
    }

}
