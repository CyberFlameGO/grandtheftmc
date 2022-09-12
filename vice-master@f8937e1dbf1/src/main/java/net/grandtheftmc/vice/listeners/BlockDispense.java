package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.vice.Vice;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;

/**
 * Created by Timothy Lampen on 3/10/2018.
 */
public class BlockDispense implements Listener {

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        Block b = event.getBlock();
        if(b.getType()== Material.DROPPER) {
            if(Vice.getInstance().getMachineManager().getMachineByLocation(b.getLocation()).isPresent())
                event.setCancelled(true);
        }
    }
}
