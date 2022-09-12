package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedvehicles.api.events.VehicleEnterEvent;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import net.grandtheftmc.core.events.PlayerFActionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

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

    }
}
