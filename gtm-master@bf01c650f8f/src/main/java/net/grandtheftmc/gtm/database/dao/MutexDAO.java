package net.grandtheftmc.gtm.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.debug.Log;

public class MutexDAO {

	/**
	 * Get the gtm user's mutex.
	 * <p>
	 * If this returns true, then the gtm user's mutex is already lent out to
	 * someone else. If this returns false, then the gtm user's mutex is not yet
	 * occupied.
	 *
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user
	 *
	 * @return {@code true} if the gtm user's mutex is already taken,
	 *         {@code false} if the gtm user's mutex is empty.
	 */
	public static boolean getGTMUserMutex(Connection conn, UUID uuid) {

		boolean mutex = false;

		String query = "SELECT mutex FROM " + Core.name() + " WHERE uuid=UNHEX('" + uuid.toString().replaceAll("-", "") + "')";

		try (ResultSet result = conn.createStatement().executeQuery(query)) {
			if (result.next()) {
				mutex = result.getBoolean("mutex");
			}
		}
		catch (SQLException exc) {
			Log.error("Core", "Error executing getUserMutex() for user identified by " + uuid.toString());
			exc.printStackTrace();
		}

		return mutex;
	}

	/**
	 * Updates the gtm user's mutex.
	 *
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user
	 * @param mutex - {@code true} if we still want to occupy the mutex,
	 *            {@code false} otherwise.
	 */
	public static void setGTMUserMutex(Connection conn, UUID uuid, boolean mutex) {

		String query = "UPDATE " + Core.name() + " SET mutex=? WHERE uuid=UNHEX('" + uuid.toString().replaceAll("-", "") + "')";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setBoolean(1, mutex);

			statement.executeUpdate();
		}
		catch (SQLException exc) {
			Log.error("Core", "Error executing setUserMutex() for user identified by " + uuid.toString());
			exc.printStackTrace();
		}
	}
}
