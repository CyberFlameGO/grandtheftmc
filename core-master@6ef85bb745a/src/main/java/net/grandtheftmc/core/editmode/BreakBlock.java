package net.grandtheftmc.core.editmode;

import net.grandtheftmc.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlock implements Listener {


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName()) && !Core.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()).hasEditMode()) {
            e.setCancelled(true);
        }
    }
}
