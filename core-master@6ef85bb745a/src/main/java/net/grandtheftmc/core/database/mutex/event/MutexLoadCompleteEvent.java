package net.grandtheftmc.core.database.mutex.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.core.database.mutex.Mutexable;

public class MutexLoadCompleteEvent extends Event {

	/** List of handlers for this event. */
	private static final HandlerList HANDLERS = new HandlerList();
	/** The mutexable object that was completely loaded */
	private final Mutexable mutexable;

	/**
	 * Construct a new MutexLoadCompleteEvent.
	 * <p>
	 * This is used as an optional event fire, AFTER
	 * LoadMutexTask#onLoadComplete() is called.
	 * 
	 * @param mutexable - the mutexable that was completely loaded
	 */
	public MutexLoadCompleteEvent(Mutexable mutexable) {
		this.mutexable = mutexable;
	}

	/**
	 * Get the mutexable that was completely loaded in this event.
	 * 
	 * @return The mutexable involved in the event.
	 */
	public Mutexable getMutexable() {
		return mutexable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	/**
	 * Get the handlers involved in the event.
	 * 
	 * @return The handlers for this event.
	 */
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
