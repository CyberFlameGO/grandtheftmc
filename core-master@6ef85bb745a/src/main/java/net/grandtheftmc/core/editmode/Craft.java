package net.grandtheftmc.core.editmode;

import net.grandtheftmc.core.Core;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class Craft implements Listener {

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if (Core.getSettings().canCraft()) return;
        e.setResult(null);
        e.setCancelled(true);
    }

}
