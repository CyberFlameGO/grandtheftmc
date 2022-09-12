package net.grandtheftmc.houses.listeners;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseChest;
import net.grandtheftmc.houses.houses.HousesManager;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.UserHouse;
import net.grandtheftmc.houses.users.UserHouseChest;

public class InventoryClose implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (inv.getType() != InventoryType.CHEST)
            return;
        Player player = (Player) e.getPlayer();
        UUID uuid = player.getUniqueId();
        String title = ChatColor.stripColor(inv.getTitle()).replace("Chest: ", "");
        String[] ids = title.split(",");
        if (ids.length != 2)
            return;
        int houseId;
        int chestId;
        try {
            houseId = Integer.parseInt(ids[0]);
            chestId = Integer.parseInt(ids[1]);
        } catch (NumberFormatException ex) {
            return;
        }
        HousesManager hm = Houses.getManager();
        House house = hm.getHouse(houseId);
        if (house == null)
            return;
        HouseChest chest = house.getChest(chestId);
        if (chest == null)
            return;
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        UserHouse userHouse = user.getUserHouse(houseId);
        if (userHouse == null)
            return;
        UserHouseChest userChest = userHouse.getChest(chestId);
        if (userChest == null || Arrays.equals(userChest.getContents(), inv.getContents()))
            return;
        userChest.setContents(inv.getContents());
        user.setOpenChest(null);
       // userChest.updateContents(uuid);
        Utils.playChestAnimation(player, chest.getLoc1(), false);
    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        HumanEntity entity = e.getPlayer();

        if (inv.getType() != InventoryType.CHEST) {
            return;
        }

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());

        if (user.hasChestOpen()) {
            user.setOpenChest(null);
        }
    }
}
