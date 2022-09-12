
package net.grandtheftmc.hub.listeners;

import net.grandtheftmc.hub.Hub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class Damage implements Listener {

	@EventHandler
	public void onDamageByEntity(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		event.getEntity().setFireTicks(0);
		event.setCancelled(true);
		switch (event.getCause()) {
		case VOID:
		case SUFFOCATION:
		case CUSTOM:
			event.getEntity().teleport(Hub.getInstance().getSpawn());
		break;

		default:
		break;
		}
	}

}
