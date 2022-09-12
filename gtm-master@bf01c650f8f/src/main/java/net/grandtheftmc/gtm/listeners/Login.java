package net.grandtheftmc.gtm.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;

public class Login implements Listener {

	/**
	 * Listens in on the async player pre login event.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

		// if the server is restarting
		if (Core.getInstance().isRestarting()) {
			event.setKickMessage(Lang.ALERTS.f("&cThe server is restarting!"));
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "&cThe server is currently restarting!");
			return;
		}

		// if the gtm plugin isn't enabled
		if (!GTM.getInstance().isEnabled()) {
			event.setKickMessage(Lang.ALERTS.f("&cWaiting on GTM..."));
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "&cWaiting on GTM...");
			return;
		}

		// if the player is transferring
		if (GTM.getInstance().getTransferingPlayers().contains(event.getUniqueId())) {
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Lang.GTM.f("&eYour data is being transfered."));
		}
	}
}
