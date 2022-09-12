package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.gtm.items.ArmorType;
import net.grandtheftmc.gtm.items.ArmorUpgrade;
import net.grandtheftmc.gtm.items.events.ArmorEquipEvent;
import net.grandtheftmc.gtm.items.events.EquipArmorType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 7/6/2017.
 */
public class ItemBreak implements Listener {

    @EventHandler
    public void armorEquipRunner(PlayerItemBreakEvent e) {
        EquipArmorType type = EquipArmorType.matchType(e.getBrokenItem());
        if(type != null){
            Player p = e.getPlayer();
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null, null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                ItemStack i = e.getBrokenItem().clone();
                i.setAmount(1);
                i.setDurability((short) (i.getDurability() - 1));
                if(type.equals(EquipArmorType.HELMET)){
                    p.getInventory().setHelmet(i);
                }else if(type.equals(EquipArmorType.CHESTPLATE)){
                    p.getInventory().setChestplate(i);
                }else if(type.equals(EquipArmorType.LEGGINGS)){
                    p.getInventory().setLeggings(i);
                }else if(type.equals(EquipArmorType.BOOTS)){
                    p.getInventory().setBoots(i);
                }
            }
        }
    }
}
