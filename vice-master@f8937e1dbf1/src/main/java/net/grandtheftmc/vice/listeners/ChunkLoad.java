package net.grandtheftmc.vice.listeners;


import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by Timothy Lampen on 2017-08-09.
 */
public class ChunkLoad implements Listener {


    @EventHandler
    protected final void onWorldRender(PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) return;
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) return;

        if(event.getTo() == null || event.getTo().getWorld() == null) return;
        if(event.getFrom().getWorld().getName().equals(event.getTo().getWorld().getName())) return;

        if(Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().getName().equals(event.getTo().getWorld().getName())).count() <= 1)
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "hd reload");
    }

    @EventHandler
    protected final void onWorldRender(PlayerJoinEvent event) {
        if(event.getPlayer() == null) return;
        if(event.getPlayer().getLocation().getWorld() == null) return;

        if(Bukkit.getOnlinePlayers().stream().filter(p -> p.getWorld().getName().equals(event.getPlayer().getLocation().getWorld().getName())).count() <= 1)
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "hd reload");
    }
}
