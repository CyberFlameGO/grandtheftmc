package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.gtm.tasks.PlayerTask;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;

public class FireListener implements Listener {

    @EventHandler
    public void blockBurnEvent(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void blockSpreadEvent(BlockSpreadEvent event) {
        if (event.getSource().getType() == Material.FIRE) event.getSource().setType(Material.AIR);
        event.setCancelled(true);
    }

    public static void clearFire() {
        PlayerTask.fireBlocks.forEach(block -> {
            block.setType(Material.AIR);
            PlayerTask.fireBlocks.remove(block);
        });
    }
}
