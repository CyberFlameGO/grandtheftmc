package net.grandtheftmc.core.users;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Timothy Lampen on 2/4/2018.
 */
public class CooldownDAO {

    public static Set<CooldownPayload> loadCooldowns(User user) {
        Set<CooldownPayload> set = new HashSet<>();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            set.addAll(loadCooldown(connection, user.getUUID(), Core.name().toLowerCase()));
            set.addAll(loadCooldown(connection, user.getUUID(), "global"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return set;
    }

    /**
     * Load cooldown information for a user.
     *
     * @param conn      - the database connection thread
     * @param uuid      - the uuid of the user
     * @param serverKey - the server key identifier
     * @return
     */
    public static Set<CooldownPayload> loadCooldown(Connection conn, UUID uuid, String serverKey) {

        Set<CooldownPayload> set = new HashSet<>();

        String query = "SELECT * FROM `user_cooldown` WHERE `uuid`=UNHEX(?) AND `server_key`=?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, uuid.toString().replace("-", ""));
            ps.setString(2, Core.name().toLowerCase());

            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    String id = result.getString("id");


                    Timestamp endTime = result.getTimestamp("endTime");
                    if (endTime.getTime() <= System.currentTimeMillis()) {
                        continue;
                    }

                    CooldownPayload payload = new CooldownPayload(id, endTime.getTime(), !serverKey.equals("global"), true);
                    set.add(payload);
                }
            }
        } catch (Exception e) {
            Core.log("[CooldownDAO] Error executing loadCooldown for uuid=" + uuid.toString() + ", serverKey=" + serverKey);
            e.printStackTrace();
        }

        return set;
    }

    public static void saveCooldowns(Connection connection, User user) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `user_cooldown` WHERE `uuid`=UNHEX(?) AND `server_key`=? OR `server_key`=?;")) {
            statement.setString(1, user.getUUID().toString().replace("-", ""));
            statement.setString(2, Core.name().toLowerCase());
            statement.setString(3, "global");
            statement.execute();

            uploadCooldowns(connection, user);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @apiNote for internal use only!
     */
    private static void uploadCooldowns(Connection connection, User user) {
        if (user.getCooldowns() == null) {
            return;
        }

        for (CooldownPayload payload : user.getCooldowns()) {
            if (payload == null) {
                continue;
            }

            if (!payload.isSaveToMySQL()) {
                continue;
            }

            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `user_cooldown` (uuid,server_key,id,endTime) VALUES (UNHEX(?),?,?,?)")) {
                statement.setString(1, user.getUUID().toString().replace("-", ""));
                statement.setString(2, payload.isServerSpecific() ? Core.name().toLowerCase() : "global");
                statement.setString(3, payload.getId());
                statement.setTimestamp(4, payload.getExpireTime());
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
