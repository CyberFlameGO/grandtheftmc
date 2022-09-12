package net.grandtheftmc.gtm.event.easter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.grandtheftmc.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public final class EasterDAO {

	protected static List<EasterEgg> getEasterEggs(Connection connection) {
		List<EasterEgg> eggs = Lists.newArrayList();
		try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM easter_egg WHERE server_key=?;")) {
			statement.setString(1, Core.name());
			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					int uniqueIdentifier = result.getInt("id");
					World world = Bukkit.getWorld(result.getString("world"));
					double x = result.getDouble("x");
					double y = result.getDouble("y");
					double z = result.getDouble("z");
					eggs.add(new EasterEgg(uniqueIdentifier, new Location(world, x, y, z)));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return eggs;
	}

	protected static EasterEgg addEasterEgg(Connection connection, Location location) {
		EasterEgg easterEgg = new EasterEgg(location);
		try (PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO easter_egg (world, x, y, z, server_key) VALUES (?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, location.getWorld().getName());
			statement.setDouble(2, location.getX());
			statement.setDouble(3, location.getY());
			statement.setDouble(4, location.getZ());
			statement.setString(5, Core.name());

			statement.execute();
			try (ResultSet result = statement.getGeneratedKeys()) {
				if (result.next()) {
					easterEgg.setUniqueIdentifier(result.getInt(1));
					return easterEgg;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected static void addUserFind(Connection connection, UUID uuid, int eggId) {
		try (PreparedStatement statement = connection.prepareStatement("INSERT INTO easter_user (uuid, egg_id, server_key) VALUES (UNHEX(?), ?, ?);")) {
			statement.setString(1, uuid.toString().replaceAll("-", ""));
			statement.setInt(2, eggId);
			statement.setString(3, Core.name());

			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected static Set<Integer> getFoundEggs(Connection connection, UUID uuid) {
		Set<Integer> list = Sets.newHashSet();
		try (PreparedStatement statement = connection.prepareStatement("SELECT egg_id FROM easter_user WHERE uuid=UNHEX(?) AND server_key=?;")) {
			statement.setString(1, uuid.toString().replaceAll("-", ""));
			statement.setString(2, Core.name());

			try (ResultSet result = statement.executeQuery()) {
				while (result.next()) {
					list.add(result.getInt(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
}
