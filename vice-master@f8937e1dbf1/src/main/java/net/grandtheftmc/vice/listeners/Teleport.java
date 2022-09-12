package net.grandtheftmc.vice.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by ThatAbstractWolf on 2017-08-24.
 */
public class Teleport implements Listener {

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {

		if (event.getPlayer().getWorld().getName().equals("spawn") && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
			event.setCancelled(true);
	}
}
