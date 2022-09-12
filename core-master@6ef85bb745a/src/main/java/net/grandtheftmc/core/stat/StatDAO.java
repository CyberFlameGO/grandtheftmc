package net.grandtheftmc.core.stat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

public class StatDAO {

	/**
	 * Create user join info record in the database.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to create the record for
	 * @param serverJoinAddress - the server address they joined with
	 * 
	 * @return {@code} true if the record was created, {@code false} otherwise.
	 */
	public static boolean createUserJoinInfo(Connection conn, UUID uuid, String serverJoinAddress) {

		// note: look at initial record is 2x serverJoinAddress, as it can be updated later
		// with on update query
		String query = "INSERT IGNORE INTO user_join_info (uuid, initial_server_address, last_server_address) VALUES (UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE last_server_address=VALUES(last_server_address), last_login=VALUES(last_login);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverJoinAddress);
			ps.setString(3, serverJoinAddress);

			ps.executeUpdate();
			return true;
		}
		catch (Exception e) {
			System.out.println("[StatDAO] An error occurred for createUserStat() for uuid=" + uuid + ", serverJoinAddress=" + serverJoinAddress);
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Get the join address the user.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to lookup
	 * 
	 * @return The string representation of the join address the user used to connect wtih, i.e. 'dev.mc-gtm.net', if one exists.
	 */
	public static Optional<String> getUserJoinAddress(Connection conn, UUID uuid) {

		//String query = "SELECT last_server_address FROM user_join_info WHERE uuid=UNHEX(?);";
		String query = "SELECT initial_server_address FROM user_join_info WHERE uuid=UNHEX(?);";
		
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			
			try (ResultSet result = ps.executeQuery()){
				if (result.next()){
					return Optional.of(result.getString("initial_server_address"));
				}
			}
		}
		catch (Exception e) {
			System.out.println("[StatDAO] An error occurred for getUserJoinAddress() for uuid=" + uuid);
			e.printStackTrace();
		}

		return Optional.empty();
	}
}
