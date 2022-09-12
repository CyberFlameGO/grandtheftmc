package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.events.ArmorEquipEvent;
import net.grandtheftmc.vice.events.EquipArmorType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 7/24/2017.
 */
public class ItemBreak implements Listener {

    @EventHandler
    public void armorEquipRunner(PlayerItemBreakEvent e) {
        Player p = e.getPlayer();
        ItemStack is = e.getBrokenItem();
        if(is.getAmount()>1 && ViceUtils.isTool(is.getType())) {
            is.setAmount(is.getAmount()-1);
            is.setDurability((short)0);
            return;
        }
        EquipArmorType type = EquipArmorType.matchType(e.getBrokenItem());
        if(type != null){
            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.BROKE, type, e.getBrokenItem(), null);
            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
            if(armorEquipEvent.isCancelled()){
                ItemStack i = is.clone();
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
