package net.grandtheftmc.core.voting.crates.events;

import net.grandtheftmc.core.voting.crates.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ThatAbstractWolf on 2017-08-08.
 */
public class CrateNearbyPlayerEvent extends Event implements Cancellable {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	private boolean cancelled;

	private final Player player;
	private final Crate crate;

	public CrateNearbyPlayerEvent(Player player, Crate crate) {

		this.player = player;
		this.crate = crate;

		this.cancelled = false;
	}

	public Player getPlayer() {
		return player;
	}

	public Crate getCrate() {
		return crate;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}
}
