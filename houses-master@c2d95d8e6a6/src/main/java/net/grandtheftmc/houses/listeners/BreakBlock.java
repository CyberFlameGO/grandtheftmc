package net.grandtheftmc.houses.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.*;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlock implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        BlockState block = e.getBlock().getState();
        if (block.getType() != Material.CHEST)
            return;
        HousesManager hm = Houses.getManager();
        Object[] houseAndChest = hm.getHouseAndChest(block.getLocation());
        if (houseAndChest == null)
            return;
        e.setCancelled(true);
        Player player = e.getPlayer();
        if (houseAndChest[0] instanceof PremiumHouse) {
            PremiumHouse house = (PremiumHouse) houseAndChest[0];
            PremiumHouseChest chest = (PremiumHouseChest) houseAndChest[1];
            if (player.hasPermission("houses.admin"))
                player.sendMessage(Utils.f(Lang.HOUSES + "&7This PremiumHouseChest with ID &a" + chest.getId() + "&7 belongs to premium house &a" + house.getId()
                        + "&7! Please remove it with &3/hc remove &a<id>&7 before breaking it!"));
        }
        House house = (House) houseAndChest[0];
        HouseChest chest = (HouseChest) houseAndChest[1];
        if (player.hasPermission("houses.admin"))
            player.sendMessage(Utils.f(Lang.HOUSES + "&7This HouseChest with ID &a" + chest.getId() + "&7 belongs to house &a" + house.getId()
                    + "&7! Please remove it with &3/hc remove &a<id>&7 before breaking it!"));
    }

}
