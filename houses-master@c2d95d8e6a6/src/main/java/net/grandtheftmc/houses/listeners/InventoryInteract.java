package net.grandtheftmc.houses.listeners;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseChest;
import net.grandtheftmc.houses.houses.HousesManager;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseChest;
import net.grandtheftmc.houses.users.HouseUser;

public class InventoryInteract implements Listener {
    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        this.onInventoryInteract(event);
    }

    @EventHandler
    private void onInventoryDrag(InventoryDragEvent event) {
        this.onInventoryInteract(event);
    }

    @EventHandler
    private void onInventoryInteract(InventoryInteractEvent event) {
        Inventory inv = event.getInventory();
        HumanEntity entity = event.getWhoClicked();

        if (inv.getType() != InventoryType.CHEST) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }
        
        if(inv.getLocation() == null) {
            return;
        }

        Player player = (Player) entity;
        HousesManager hm = Houses.getManager();
        Object[] houseAndChest = hm.getHouseAndChest(new Location(inv.getLocation().getWorld(), inv.getLocation().getBlockX(), inv.getLocation().getBlockY(), inv.getLocation().getBlockZ()));

        if (houseAndChest == null || houseAndChest[0] == null || houseAndChest[1] == null) {
            return;
        }

        if (houseAndChest[0] instanceof PremiumHouse) {
            this.managePremiumHouse(event, player, (PremiumHouse) houseAndChest[0], (PremiumHouseChest) houseAndChest[1]);
        } else {
            this.manageHouse(event, player, (House) houseAndChest[0], (HouseChest) houseAndChest[1]);
        }
    }

    private void managePremiumHouse(InventoryInteractEvent event, Player player, PremiumHouse house, PremiumHouseChest chest) {
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());

        if (user.hasChestOpen() && user.getOpenChest() == chest) {
            return;
        }

        event.setCancelled(true);
    }

    private void manageHouse(InventoryInteractEvent event, Player player, House house, HouseChest chest) {
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());

        if (user.hasChestOpen() && user.getOpenChest() == chest) {
            return;
        }

        event.setCancelled(true);
    }
}