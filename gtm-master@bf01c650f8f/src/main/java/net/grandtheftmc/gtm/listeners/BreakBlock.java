package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakBlock implements Listener{

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        switch (block.getType()) {
         case CHEST:
             if (GTM.getCrateManager().getCrate(block.getLocation()) == null) return;
             event.setCancelled(true);
             event.getPlayer().sendMessage(Lang.LOOTCRATES.f("&7You can't break this Loot Crate!"));
             default:
                 break;
         }
     }

}
