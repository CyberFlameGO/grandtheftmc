package net.grandtheftmc.core.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.grandtheftmc.core.Core;

/**
 * Data access object for event specific data.
 * 
 * @author sbahr
 */
public class EventDAO {

	/**
	 * Get the active event that the specified key as.
	 * <p>
	 * For example, a serverKey would be "gtm1", and if there is an active event
	 * for this server, it will return the event type that is active.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the key of the server to lookup the event for
	 * 
	 * @return The event data that is currently active for the specified event.
	 */
	public static Optional<EventData> getActiveEvent(Connection conn, String serverKey) {

		String query = "SELECT * FROM event WHERE server_key=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, serverKey);

			try (ResultSet result = ps.executeQuery()) {
				
				if (result.next()){
					EventType eventType = EventType.fromID(result.getString("event_type")).orElse(null);
					
					// convert string to json object
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(result.getString("data"));
					
					Timestamp startTime = result.getTimestamp("start_time");
					Timestamp endTime = result.getTimestamp("end_time");
					
					return Optional.of(new EventData(serverKey, eventType, json, startTime, endTime));
				
				}
			}
		}
		catch (Exception exc) {
			Core.log("[EventDAO] Error executing getActiveEvent() for serverKey=" + serverKey);
			exc.printStackTrace();
		}

		return Optional.empty();
	}
	
	/**
	 * Schedules an event at the specified start/end time for the given server.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the server key for the server to schedule the event on
	 * @param eventType - the type of the event to run
	 * @param data - the data associated with the event
	 * @param startTime - the start time of the event
	 * @param endTime - the end time of the event
	 * 
	 * @return {@code true} if the query ran, {@code false} otherwise.
	 */
	public static boolean scheduleEvent(Connection conn, String serverKey, EventType eventType, JSONObject data, Timestamp startTime, Timestamp endTime) {
		
		String query = "INSERT INTO event (server_key, event_type, data, start_time, end_time) VALUES (?, ?, ?, ?, ?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, serverKey);
			ps.setString(2, eventType.getId());
			ps.setString(3, data.toJSONString());
			ps.setTimestamp(4, startTime);
			ps.setTimestamp(5, endTime);

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[EventDAO] Error executing scheduleEvent() for serverKey=" + serverKey + " and eventType=" + eventType);
			exc.printStackTrace();

			return false;
		}
	}
	
	/**
	 * Schedules an event at the specified start/end time for the given server.
	 * <p>
	 * Note: This uses a default start time according to the current timestamp.
	 * </p>
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the server key for the server to schedule the event on
	 * @param eventType - the type of the event to run
	 * @param data - the data associated with the event
	 * @param endTime - the end time of the event
	 * 
	 * @return {@code true} if the query ran, {@code false} otherwise.
	 */
	public static boolean scheduleEvent(Connection conn, String serverKey, EventType eventType, JSONObject data, Timestamp endTime) {
		
		String query = "INSERT INTO event (server_key, event_type, data, start_time, end_time) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, serverKey);
			ps.setString(2, eventType.getId());
			ps.setString(3, data.toJSONString());
			ps.setTimestamp(4, endTime);

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[EventDAO] Error executing scheduleEvent() for serverKey=" + serverKey + " and eventType=" + eventType);
			exc.printStackTrace();

			return false;
		}
	}

	/**
	 * Clears the active event for the specified server key.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the key of the serverup to clear the event for
	 * 
	 * @return {@code true} if the query ran, {@code false} otherwise.
	 */
	public static boolean clearActiveEvent(Connection conn, String serverKey) {
		
		String query = "DELETE FROM event WHERE server_key=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, serverKey);

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[EventDAO] Error executing clearActiveEvent() for serverKey=" + serverKey);
			exc.printStackTrace();

			return false;
		}
	}

	/**
	 * Clear all the active events, regardless of server id.
	 * 
	 * @param conn - the database connection thread
	 * 
	 * @return {@code true} if the query ran, {@code false} otherwise.
	 */
	public static boolean clearAllActiveEvents(Connection conn) {
		
		String query = "DELETE FROM event;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[EventDAO] Error executing clearAllActiveEvents()");
			exc.printStackTrace();

			return false;
		}
	}
}

