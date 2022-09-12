package net.grandtheftmc.gtm.listeners;

import com.j0ach1mmall3.wastedvehicles.api.events.VehicleEnterEvent;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import net.grandtheftmc.core.events.PlayerFActionEvent;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.*;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.UUID;

public class SwapHandItems implements Listener {

    @EventHandler
    public void onPlayerFAction(PlayerFActionEvent event) {
        Player player = event.getPlayer();
        Block targetBlock;
        if (player.getTargetBlock((Set<Material>) null, 1).getType() != Material.AIR) {
            targetBlock = player.getTargetBlock((Set<Material>) null, 1);
        } else if (player.getTargetBlock((Set<Material>) null, 2).getType() != Material.AIR) {
            targetBlock = player.getTargetBlock((Set<Material>) null, 2);
        } else {
            targetBlock = player.getTargetBlock((Set<Material>) null, 3);
        }
        player.getWorld().getNearbyEntities(targetBlock.getLocation(), 2, 2, 2).forEach(entity -> {
            if (entity.getType() != EntityType.ARMOR_STAND) return;
            if (entity.hasMetadata("WastedVehicle")) {
                WastedVehicle wastedVehicle = (WastedVehicle) entity.getMetadata("WastedVehicle").get(0).value();
                VehicleEnterEvent vehicleEnterEvent = new VehicleEnterEvent(wastedVehicle, player, (ArmorStand) entity);
                Bukkit.getPluginManager().callEvent(vehicleEnterEvent);
                if (!vehicleEnterEvent.isCancelled()) wastedVehicle.onRightClick((ArmorStand) entity, player);
            }
        });
        if (targetBlock.getType() == Material.IRON_DOOR || targetBlock.getType() == Material.IRON_DOOR_BLOCK) {
            UUID uuid = player.getUniqueId();
            BlockState state = targetBlock.getState();
            HousesManager hm = Houses.getManager();
            HouseUser houseUser = Houses.getUserManager().getLoadedUser(uuid);
            Block underneath = targetBlock.getRelative(BlockFace.DOWN);
            if (underneath.getType() == Material.IRON_DOOR_BLOCK)
                state = underneath.getState();
            Location loc = state.getLocation();
            Object[] houseAndDoor = hm.getHouseAndDoor(loc);
            if (houseAndDoor == null) return;
            if (houseAndDoor[0] instanceof PremiumHouse) {
                PremiumHouse house = (PremiumHouse) houseAndDoor[0];
                if (houseUser.isTeleporting()) return;
                if (!house.hasAccess(player, houseUser) || player.isSneaking()) {
                    HouseUtils.openPremiumHouseMenu(player, house, houseUser);
                    return;
                }
                PremiumHouseDoor door = (PremiumHouseDoor) houseAndDoor[1];
                houseUser.teleportInOrOutPremiumHouse(player, door);
                return;
            }
            House house = (House) houseAndDoor[0];
            if (!houseUser.ownsHouse(house.getId())) {
                HouseUtils.openHouseMenu(player, house, houseUser);
                return;
            }
            if (houseUser.isTeleporting()) return;
            if (player.isSneaking()) {
                HouseUtils.openHouseMenu(player, house, houseUser);
                return;
            }
            HouseDoor door = (HouseDoor) houseAndDoor[1];
            houseUser.teleportInOrOutHouse(player, door);
        }
    }
}
