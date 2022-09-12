package net.grandtheftmc.hub.listeners;

import net.grandtheftmc.hub.Hub;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Death implements Listener {

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		Player player = e.getEntity();
		player.setHealth(20);
		player.teleport(Hub.getInstance().getSpawn());
	}
}
