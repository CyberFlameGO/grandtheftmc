package net.grandtheftmc.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import net.grandtheftmc.core.Core;

public class ChunkLoad implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if ((Core.getSettings().stopChunkLoad(e.getWorld().getName()) && e.isNewChunk()) || (Core.getSettings().stopLoadDefaultWorld() && "world".equals(e.getWorld().getName())))
                e.getChunk().unload(false,false);
    }

}