package net.grandtheftmc.core.database.mutex.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.core.database.mutex.Mutexable;

public class AsyncMutexLoadEvent extends Event {

	/** List of handlers for this event. */
	private static final HandlerList HANDLERS = new HandlerList();
	/** The mutexable object that was loaded */
	private final Mutexable mutexable;
	
	/**
	 * Construct a new AsyncMutexLoadEvent.
	 * <p>
	 * This is used as a notification to the server that a mutexable was loaded.
	 * <p>
	 * This event occurs before LoadMutexTask#onLoadComplete() is called.
	 * 
	 * @param mutexable - the mutexable that was loaded
	 */
	public AsyncMutexLoadEvent(Mutexable mutexable) {
		super(true);
		this.mutexable = mutexable;
	}

	/**
	 * Get the mutexable that was loaded in this event.
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

