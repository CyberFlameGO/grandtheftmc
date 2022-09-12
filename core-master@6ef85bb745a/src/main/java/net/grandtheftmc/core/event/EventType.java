package net.grandtheftmc.core.event;

import java.util.Optional;

public enum EventType {

	HALLOWEEN("HALLOWEEN"),
	CHRISTMAS("CHRISTMAS"),
	EASTER("EASTER"),
	;

	/** The ID of the event type */
	private final String id;

	/**
	 * Create a new event type constant.
	 * 
	 * @param id - the id of the event
	 */
	EventType(String id) {
		this.id = id;
	}

	/**
	 * Get the ID of the event.
	 * 
	 * @return The id of the event.
	 */
	public final String getId() {
		return id;
	}

	/**
	 * Get the event type from the specified id.
	 * 
	 * @param id - the id of the event
	 * 
	 * @return The event type with the specified id, if one exists.
	 */
	public static Optional<EventType> fromID(String id) {
		for (EventType et : values()) {
			if (et.getId().equalsIgnoreCase(id)) {
				return Optional.of(et);
			}
		}

		return Optional.empty();
	}
}

