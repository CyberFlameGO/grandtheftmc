package net.grandtheftmc.core.editmode;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import net.grandtheftmc.core.Core;

public class HangingBreak implements Listener {

    @EventHandler
    public void onEntityDeath(HangingBreakEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getEntity().getWorld().getName()) && e.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION)
            e.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreak(HangingBreakByEntityEvent e) {
        if (!Core.getWorldManager().usesEditMode(e.getEntity().getWorld().getName()))
            return;
        Entity hanging = e.getEntity();
        switch (hanging.getType()) {
            case ITEM_FRAME:
            case PAINTING:
                Entity remover = e.getRemover();
                if (remover instanceof Player
                        && Core.getUserManager().getLoadedUser(remover.getUniqueId()).hasEditMode())
                    return;
                e.setCancelled(true);
                return;
            default:
        }
    }
}
