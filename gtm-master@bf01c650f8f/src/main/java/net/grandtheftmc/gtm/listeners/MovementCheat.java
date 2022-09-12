package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.anticheat.check.CheatType;
import net.grandtheftmc.core.anticheat.event.MovementCheatEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Timothy Lampen on 2/8/2018.
 */
public class MovementCheat implements Listener {

    @EventHandler
    public void onMovementCheat(MovementCheatEvent event) {
        Player player = event.getPlayerData().getPlayer();

        if(event.getCheatType().getType()== CheatType.Type.FLIGHT) {
            if (player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.ELYTRA) {
                if (player.isGliding())
                    event.setCancelled(true);//basically cancel the trigger of cheats check if they are gliding using an elytra
            }
        }
    }
}
