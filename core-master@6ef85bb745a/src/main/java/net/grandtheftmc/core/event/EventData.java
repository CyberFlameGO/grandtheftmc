package net.grandtheftmc.core.event;

import java.sql.Timestamp;

import org.json.simple.JSONObject;

/**
 * Data object for events, like halloween, easter, etc. Typically read from the
 * database.
 * 
 * @author sbahr
 */
public class EventData {

	/** The key of the server for this event */
	private final String serverKey;
	/** The event type for this data */
	private final EventType eventType;
	/** The JSON data for this event */
	private final JSONObject data;
	/** The start time for this event */
	private final Timestamp startTime;
	/** The end time for this event */
	private final Timestamp endTime;

	/**
	 * Create the event data for this event.
	 * 
	 * @param serverKey - the server key for this event, typically something
	 *            like "gtm1"
	 * @param eventType - the type of the event to do
	 * @param jsonData - the attached json data associated with this event
	 * @param startTime - the start time for this event
	 * @param endTime - the end time for this event
	 */
	public EventData(String serverKey, EventType eventType, JSONObject jsonData, Timestamp startTime, Timestamp endTime) {
		this.serverKey = serverKey;
		this.eventType = eventType;
		this.data = jsonData;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Get the server key for this event.
	 * <p>
	 * This is typically something like "gtm1".
	 * </p>
	 * 
	 * @return The server key for this event.
	 */
	public String getServerKey() {
		return serverKey;
	}

	/**
	 * Get the type of the event.
	 * 
	 * @return The type of the event for this event data.
	 */
	public EventType getEventType() {
		return eventType;
	}

	/**
	 * Get the associated data for this event.
	 * <p>
	 * This can be attached attributes, or values to help make this event
	 * dynamic.
	 * </p>
	 * 
	 * @return The associated data for this event.
	 */
	public JSONObject getData() {
		return data;
	}

	/**
	 * Get the start time for this event.
	 * 
	 * @return The start time for this event.
	 */
	public Timestamp getStartTime() {
		return startTime;
	}

	/**
	 * Get the end time for this event.
	 * 
	 * @return The end time for this event.
	 */
	public Timestamp getEndTime() {
		return endTime;
	}
}

