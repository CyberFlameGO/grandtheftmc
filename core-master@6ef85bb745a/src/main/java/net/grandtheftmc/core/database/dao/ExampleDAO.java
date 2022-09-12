package net.grandtheftmc.core.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.servers.Server;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.users.UserRank;

/**
 * Serves as an example of how DAOs should work.
 * 
 * @author sbahr
 */
public class ExampleDAO {

	/**
	 * Example usage when running on a sync thread.
	 */
	public static void exampleUsageInCodeSync() {

		try (Connection conn = BaseDatabase.getInstance().getConnection()) {
			ExampleDAO.setName(conn, UUID.randomUUID(), "HeyThere");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Example usage when wanting to update something async, not needing result
	 * return.
	 * 
	 * @param plugin - the plugin instance
	 */
	public static void exampleUsageInCodeAsyncUpdate(Plugin plugin) {

		// this will update the username off main thread
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

			try (Connection conn = BaseDatabase.getInstance().getConnection()) {
				ExampleDAO.setName(conn, UUID.randomUUID(), "HeyThere");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Example usage when wanting an async fetch of information.
	 * 
	 * @param plugin - the plugin instance
	 */
	public static void exampleUsageInCodeAsyncFetch(Plugin plugin) {

		// this will update the username off main thread
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

			// build result OUTSIDE of try/with resource so connection
			// closes
			List<Server> servers = new ArrayList<>();
			try (Connection conn = BaseDatabase.getInstance().getConnection()) {
				servers.addAll(ExampleDAO.getServers(conn));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			// if we need to get back on sync
			Bukkit.getScheduler().runTask(plugin, () -> {
				
				if (!servers.isEmpty()){
					servers.forEach(s -> {
						Bukkit.broadcastMessage("Server name: " + s.getName());
					});
				}
			});
		});
	}

	/**
	 * Sets the name of a specified uuid.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to set the name for
	 * @param name - the new name to set
	 * 
	 * @return {@code true} if the query successfully ran, {@code false}
	 *         otherwise.
	 */
	public static boolean setName(Connection conn, UUID uuid, String name) {

		String query = "UPDATE user SET name=? WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, name);
			ps.setString(2, uuid.toString().replaceAll("-", ""));
			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[ExampleDAO] Unable to execute setName() for uuid=" + uuid.toString() + " and name=" + name);
			exc.printStackTrace();
		}

		return false;
	}

	/**
	 * Delete from the database the specified user.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to delete
	 * 
	 * @return {@code true} if the query ran, {@code false} otherwise.
	 */
	public static boolean deleteUser(Connection conn, UUID uuid) {

		String query = "DELETE FROM user WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.executeUpdate();

			return true;
		}
		catch (SQLException exc) {
			Core.log("[ExampleDAO] Unable to execute deleteUser() for uuid=" + uuid.toString());
			exc.printStackTrace();
		}

		return false;
	}

	/**
	 * Get a list of servers that are active in the database.
	 * 
	 * @param conn - the database connection thread
	 * 
	 * @return A list of servers that were located in the database.
	 */
	public static List<Server> getServers(Connection conn) {

		List<Server> servers = new ArrayList<>();
		String query = "SELECT * FROM servers";

		try (PreparedStatement ps = conn.prepareStatement(query)) {

			try (ResultSet result = ps.executeQuery()) {
				while (result.next()) {
					Server server = new Server(result.getString("name"), ServerType.getType(result.getString("type")), result.getInt("number"), result.getString("ip"), result.getInt("port"), UserRank.getUserRankOrNull(result.getString("rankToJoin")));
					servers.add(server);
				}
			}
		}
		catch (SQLException exc) {
			Core.log("[ExampleDAO] Unable to execute getServers()");
			exc.printStackTrace();
		}

		return servers;
	}

	/**
	 * Get the server object given the specified server id, or server number.
	 * 
	 * @param conn - the database connection thread
	 * @param serverID - the id of the server to get
	 * 
	 * @return The server, if one was found.
	 */
	public static Optional<Server> getServer(Connection conn, int serverID) {

		String query = "SELECT * FROM servers WHERE number=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, serverID);

			try (ResultSet result = ps.executeQuery()) {
				if (result.next()) {
					Server server = new Server(result.getString("name"), ServerType.getType(result.getString("type")), result.getInt("number"), result.getString("ip"), result.getInt("port"), UserRank.getUserRankOrNull(result.getString("rankToJoin")));
					return Optional.of(server);
				}
			}
		}
		catch (SQLException exc) {
			Core.log("[ExampleDAO] Unable to execute getServers()");
			exc.printStackTrace();
		}

		return Optional.empty();
	}
}
