package net.grandtheftmc.core.voting.crates.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.grandtheftmc.core.voting.crates.events.CrateNearbyPlayerEvent;
import org.bukkit.util.Vector;

/**
 * Created by ThatAbstractWolf on 2017-08-08.
 */
public class CrateNearbyListener implements Listener {

	@EventHandler
	public void playerNearCrate(CrateNearbyPlayerEvent event) {

		Player player = event.getPlayer();

		if (event.getCrate() != null && event.getCrate().isBeingOpened() && event.getCrate().getOpeningCrateUUID().equals(player.getUniqueId())) return;

		Vector velocity = player.getLocation().getDirection();

		velocity.setY(0.2);

		player.setVelocity(velocity.multiply(-0.3));
	}
}
