package net.grandtheftmc.core.transaction.state.user;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.core.transaction.state.StateTransactionEvent;

public class UserStateTransactionEvent extends StateTransactionEvent<UserStateTransaction> {

	/** List of handlers for this event */
	private static final HandlerList HANDLERS = new HandlerList();
	/** The player to run the event for */
	private final Player player;

	/**
	 * Create a new UserStateTransaction event.
	 * 
	 * @param player - the player involved in the event
	 * @param transaction - the transaction
	 */
	public UserStateTransactionEvent(Player player, UserStateTransaction transaction) {
		super(transaction);
		this.player = player;
	}

	/**
	 * Get the player involved in the event.
	 * 
	 * @return The player involved in the event.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	/**
	 * Get the handlers involved in this event.
	 * 
	 * @return The handlers involved in this event.
	 */
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
