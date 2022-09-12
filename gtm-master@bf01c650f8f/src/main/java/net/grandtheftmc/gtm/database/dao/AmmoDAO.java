package net.grandtheftmc.gtm.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.gtm.items.AmmoType;

public class AmmoDAO {

//	CREATE TABLE IF NOT EXISTS user_ammo(
//			uuid BINARY(16) NOT NULL, 
//			server_key VARCHAR(10) NOT NULL, 
//			ammo VARCHAR(16) NOT NULL, 
//			amount INT NOT NULL, 
//			PRIMARY KEY (uuid, server_key, ammo), 
//			FOREIGN KEY (uuid) REFERENCES user(uuid) ON DELETE CASCADE
//			);

	/**
	 * Get all the ammo for the given uuid.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user
	 * @param serverKey - the serverKey to lookup
	 * 
	 * @return A mapping of ammo to amount that exists in the serverKey lookup
	 *         for the user.
	 */
	public static Map<AmmoType, Integer> getAllAmmo(Connection conn, UUID uuid, String serverKey) {

		Map<AmmoType, Integer> ammo = new HashMap<>();

		String query = "SELECT ammo, amount FROM user_ammo WHERE uuid=UNHEX(?) AND server_key=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverKey);

			try (ResultSet result = ps.executeQuery()) {
				while (result.next()) {
					String ammoID = result.getString("ammo");
					int amount = result.getInt("amount");

					AmmoType ammoType = AmmoType.getAmmoTypeByID(ammoID).orElse(null);
					if (ammoType != null) {
						ammo.put(ammoType, amount);
					}
				}
			}
		}
		catch (Exception e) {
			Core.log("[AmmoDAO] Unable to getAllAmmo() for uuid=" + uuid.toString() + ", serverKey=" + serverKey);
			e.printStackTrace();
		}

		return ammo;
	}

	/**
	 * Get the ammo amount for the given uuid and ammo type.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user
	 * @param serverKey - the serverKey to lookup
	 * @param ammoType - the ammo type to lookup
	 * 
	 * @return The amount of ammo for the given ammo type for the given uuid.
	 */
	public static Integer getAmmo(Connection conn, UUID uuid, String serverKey, AmmoType ammoType) {

		String query = "SELECT amount FROM user_ammo WHERE uuid=UNHEX(?) AND server_key=? AND ammo=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverKey);
			ps.setString(3, ammoType.getId());

			try (ResultSet result = ps.executeQuery()) {
				if (result.next()) {
					return result.getInt("amount");
				}
			}
		}
		catch (Exception e) {
			Core.log("[AmmoDAO] Unable to getAmmo() for uuid=" + uuid.toString() + ", serverKey=" + serverKey + ", ammo=" + ammoType.getId());
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * Save the ammo for the given uuid and ammo type.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user
	 * @param serverKey - the serverKey to lookup
	 * @param ammoType - the ammo type to lookup
	 * @param amount - the amount of the ammo to save
	 * 
	 * @return {@code true} if the ammo was saved, {@code false} otherwise.
	 */
	public static boolean saveAmmo(Connection conn, UUID uuid, String serverKey, AmmoType ammoType, int amount) {

		String query = "INSERT IGNORE INTO user_ammo (uuid, server_key, ammo, amount) VALUES (UNHEX(?), ?, ?, ?) ON DUPLICATE KEY UPDATE amount=VALUES(amount);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverKey);
			ps.setString(3, ammoType.getId());
			ps.setInt(4, amount);

			ps.executeUpdate();
			return true;
		}
		catch (Exception e) {
			Core.log("[AmmoDAO] Unable to saveAmmo() for uuid=" + uuid.toString() + ", serverKey=" + serverKey + ", ammo=" + ammoType.getId() + ", amount=" + amount);
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Save all the ammo for the given uuid and ammo map.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user
	 * @param serverKey - the serverKey to lookup
	 * @param ammo - the ammo mapping
	 * 
	 * @return {@code true} if all ammo was saved, {@code false} if at least one
	 *         had an issue saving.
	 */
	public static boolean saveAllAmmo(Connection conn, UUID uuid, String serverKey, Map<AmmoType, Integer> ammo) {

		boolean success = true;

		for (AmmoType at : ammo.keySet()) {
			Integer amount = ammo.get(at);

			try {
				saveAmmo(conn, uuid, serverKey, at, amount);
			}
			catch (Exception e) {
				Core.log("[AmmoDAO] Unable to saveAllAmmo() for uuid=" + uuid.toString() + ", serverKey=" + serverKey);
				e.printStackTrace();
				success = false;
			}
		}

		return success;
	}
}
