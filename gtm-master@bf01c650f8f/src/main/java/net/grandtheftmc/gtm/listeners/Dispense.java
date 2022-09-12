package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.gtm.items.events.ArmorEquipEvent;
import net.grandtheftmc.gtm.items.events.EquipArmorType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;

/**
 * Created by Timothy Lampen on 2017-08-11.
 */
public class Dispense implements Listener {

    @EventHandler
    public void onDispense(BlockDispenseEvent e){
        EquipArmorType type = EquipArmorType.matchType(e.getItem());
        if(EquipArmorType.matchType(e.getItem()) != null){
            Location loc = e.getBlock().getLocation();
            for(Player p : loc.getWorld().getPlayers()){
                if(loc.getBlockY() - p.getLocation().getBlockY() >= -1 && loc.getBlockY() - p.getLocation().getBlockY() <= 1){
                    if(p.getInventory().getHelmet() == null && type.equals(EquipArmorType.HELMET) || p.getInventory().getChestplate() == null && type.equals(EquipArmorType.CHESTPLATE) || p.getInventory().getLeggings() == null && type.equals(EquipArmorType.LEGGINGS) || p.getInventory().getBoots() == null && type.equals(EquipArmorType.BOOTS)){
                        org.bukkit.block.Dispenser dispenser = (org.bukkit.block.Dispenser) e.getBlock().getState();
                        org.bukkit.material.Dispenser dis = (org.bukkit.material.Dispenser) dispenser.getData();
                        BlockFace directionFacing = dis.getFacing();
                        // Someone told me not to do big if checks because it's hard to read, look at me doing it -_-
                        if(directionFacing == BlockFace.EAST && p.getLocation().getBlockX() != loc.getBlockX() && p.getLocation().getX() <= loc.getX() + 2.3 && p.getLocation().getX() >= loc.getX() || directionFacing == BlockFace.WEST && p.getLocation().getX() >= loc.getX() - 1.3 && p.getLocation().getX() <= loc.getX() || directionFacing == BlockFace.SOUTH && p.getLocation().getBlockZ() != loc.getBlockZ() && p.getLocation().getZ() <= loc.getZ() + 2.3 && p.getLocation().getZ() >= loc.getZ() || directionFacing == BlockFace.NORTH && p.getLocation().getZ() >= loc.getZ() - 1.3 && p.getLocation().getZ() <= loc.getZ()){
                            ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DISPENSER, EquipArmorType.matchType(e.getItem()), null, e.getItem(), null);
                            Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                            if(armorEquipEvent.isCancelled()){
                                e.setCancelled(true);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
