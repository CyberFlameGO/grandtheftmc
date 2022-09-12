package net.grandtheftmc.core.database.mutex.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.core.database.mutex.Mutexable;

public class AsyncMutexSaveEvent extends Event {

	/** List of handlers for this event. */
	private static final HandlerList HANDLERS = new HandlerList();
	/** The mutexable object that was saved */
	private final Mutexable mutexable;

	/**
	 * Construct a new AsyncMutexSaveEvent.
	 * <p>
	 * This is used as a notification to the server that a mutexable was saved.
	 * <p>
	 * This event occurs before SaveMutexTask#onLoadComplete() is called.
	 * 
	 * @param mutexable - the mutexable that was saved
	 */
	public AsyncMutexSaveEvent(Mutexable mutexable) {
		super(true);
		this.mutexable = mutexable;
	}

	/**
	 * Get the mutexable that was saved in this event.
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
