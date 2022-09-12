package net.grandtheftmc.gtm.event.halloween;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.grandtheftmc.core.Core;

/**
 * Data access object for halloween event specific data.
 * 
 * @author sbahr
 */
public class HalloweenDAO {

	/**
	 * Get a set of ids for the specified user where each id is the id of a
	 * premium house that has already been redeemed by the user.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user
	 * 
	 * @return A set of integers where each integer is the id of a premium
	 *         house.
	 */
	public static Set<Integer> getRedeemedHouses(Connection conn, UUID uuid) {

		Set<Integer> houses = new HashSet<>();

		String query = "SELECT house_id from event_halloween WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));

			try (ResultSet result = ps.executeQuery()) {
				while (result.next()) {
					houses.add(result.getInt("house_id"));
				}
			}
		}
		catch (SQLException exc) {
			Core.log("[HalloweenDAO] Error executing getRedeemedHouses() for uuid=" + uuid.toString());
			exc.printStackTrace();
		}

		return houses;
	}

	/**
	 * Create a redeem house transaction. This is so we mark a house as redeemed.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to create the redeem for
	 * @param houseID - the id of the house
	 * 
	 * @return {@code true} if the transaction was ran, {@code false} otherwise.
	 */
	public static boolean createRedeemedHouse(Connection conn, UUID uuid, int houseID) {

		String query = "INSERT IGNORE INTO event_halloween (uuid, house_id) VALUES (UNHEX(?), ?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setInt(2, houseID);

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[HalloweenDAO] Error executing createRedeemedHouse() for uuid=" + uuid.toString() + " and houseID=" + houseID);
			exc.printStackTrace();

			return false;
		}
	}
}
