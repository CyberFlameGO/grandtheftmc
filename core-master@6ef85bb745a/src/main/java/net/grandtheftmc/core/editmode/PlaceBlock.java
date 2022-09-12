package net.grandtheftmc.core.editmode;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceBlock implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (Core.getWorldManager().usesEditMode(player.getWorld().getName()) && !Core.getUserManager().getLoadedUser(player.getUniqueId()).hasEditMode()) {
            e.setCancelled(true);
        }
    }
}
