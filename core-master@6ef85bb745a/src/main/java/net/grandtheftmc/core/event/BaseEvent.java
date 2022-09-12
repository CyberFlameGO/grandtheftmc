package net.grandtheftmc.core.event;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Implementation of the interface Event, with basic handles for an event.
 * 
 * @author sbahr
 */
public abstract class BaseEvent implements Event {

	/** The owning plugin */
	private final Plugin plugin;
	/** The identification for this event */
	private final String id;
	/** The state of this event */
	private EventState state;
	/** The time the event started in millis */
	private long startTimeMillis;
	/** The time the event is supposed to end, in millis */
	private long endTimeMillis;

	/**
	 * Construct a new BaseEvent.
	 * <p>
	 * This is the implementation of a generic event.
	 * 
	 * @param plugin - the owning plugin
	 * @param id - the id to call this event
	 * @param startTime - the time, in millis since epoch, that this event starts
	 * @param endTime - the time, in millis since epoch, that this event ends
	 */
	public BaseEvent(Plugin plugin, String id, long startTime, long endTime) {
		this.plugin = plugin;
		this.id = id;
		this.startTimeMillis = startTime;
		this.endTimeMillis = endTime;

		// initialize
		init();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		Bukkit.getLogger().log(Level.INFO, "Initializing event=" + getId());

		setState(EventState.CONSTRUCTED);

		// call on init for implementation
		onInit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		Bukkit.getLogger().log(Level.INFO, "Starting event=" + getId());

		setState(EventState.ENABLED);

		// call on start for implementation
		onStart();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void end() {
		Bukkit.getLogger().log(Level.INFO, "Ending event=" + getId());

		setState(EventState.DISABLED);

		// call on end for implementation
		onEnd();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setState(EventState eventState) {
		this.state = eventState;
	}

	/**
	 * Get the start time of this event.
	 * 
	 * @return The start time of this event.
	 */
	public long getStartTime() {
		return startTimeMillis;
	}

	/**
	 * Get the end time of this event.
	 * 
	 * @return The end time of this event.
	 */
	public long getEndTime() {
		return endTimeMillis;
	}

	/**
	 * Get the owning plugin.
	 * 
	 * @return The owning plugin for this event.
	 */
	@Override
	public final Plugin getPlugin() {
		return plugin;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return id;
	}
}
