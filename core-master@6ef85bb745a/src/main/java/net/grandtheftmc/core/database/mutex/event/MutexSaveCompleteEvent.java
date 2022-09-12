package net.grandtheftmc.core.database.mutex.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.grandtheftmc.core.database.mutex.Mutexable;

public class MutexSaveCompleteEvent extends Event {

	/** List of handlers for this event. */
	private static final HandlerList HANDLERS = new HandlerList();
	/** The mutexable object that was completely saved */
	private final Mutexable mutexable;

	/**
	 * Construct a new MutexSaveCompleteEvent.
	 * <p>
	 * This is used as an optional event fire, AFTER
	 * SaveMutexTask#onSaveComplete() is called.
	 * 
	 * @param mutexable - the mutexable that was completely saved
	 */
	public MutexSaveCompleteEvent(Mutexable mutexable) {
		this.mutexable = mutexable;
	}

	/**
	 * Get the mutexable that was completely saved in this event.
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
