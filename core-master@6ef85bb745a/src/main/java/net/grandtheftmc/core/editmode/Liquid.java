package net.grandtheftmc.core.editmode;

import net.grandtheftmc.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class Liquid implements Listener {

    @EventHandler
    public void onFillBucket(PlayerBucketFillEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlockClicked().getWorld().getName()) && !Core.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()).hasEditMode()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEmptyBucket(PlayerBucketEmptyEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlockClicked().getWorld().getName()) && !Core.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()).hasEditMode()) {
            e.setCancelled(true);
        }
    }
}
