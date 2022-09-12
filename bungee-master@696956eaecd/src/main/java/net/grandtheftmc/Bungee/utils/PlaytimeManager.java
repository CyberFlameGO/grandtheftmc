package net.grandtheftmc.Bungee.utils;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.database.BaseDatabase;
import net.grandtheftmc.Bungee.users.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlaytimeManager {

    //How many days to store session history for.
    private static final int sessionHistoryDays = 7;
    private static final String tableName = "playtime";
    //Map UUID -> Millis() At connect time to track session time.
    private static Map<UUID, Long> sessions = new HashMap<>();

    /**
     * Mark the beginning of a playtime session for the connecting player.
     *
     * @param uuid The player whose playtime we wish to track.
     */
    public static void beginSession(String uuid) {
        sessions.put(UUID.fromString(uuid), System.currentTimeMillis());
    }

    /**
     * End the playtime session, and store the result in the database.
     *
     * @param proxiedPlayer The player whose playtime we wish to track.
     */
    public static void endSession(ProxiedPlayer proxiedPlayer) {
        if (sessions.containsKey(proxiedPlayer.getUniqueId())) {
            long now = System.currentTimeMillis();
            long elapsed = now - sessions.remove(proxiedPlayer.getUniqueId());

            Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {

                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                    String query = "INSERT INTO " + tableName + " (lastname,uuid,sessiontime,sessiondate) VALUES (?, ?, ?, ?);";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, proxiedPlayer.getName());
                        statement.setString(2, proxiedPlayer.getUniqueId().toString());
                        statement.setLong(3, elapsed);
                        statement.setLong(4, now);
                        statement.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * This function will delete any rows in the DB with session dates older than a week.
     * Call this every 24hr and on startup.
     */
    public static void purgeOldSessions() {
        Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
            long threshold = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * sessionHistoryDays);

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableName + " WHERE sessionDate<=" + threshold)) {
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Query the playtime of another user.
     *
     * @param p      The player issuing the command.
     * @param target The name of the user to lookup.
     */
    public static void lookupPlaytime(ProxiedPlayer p, String target) {
        Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {

            long threshold = System.currentTimeMillis() - (1000 * 60 * 60 * 24 * sessionHistoryDays);
            long totalTime = 0;

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT lastname,sessiontime,sessiondate FROM " + tableName + " WHERE lastname='" + target + "' AND sessiondate>" + threshold + ";")) {
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.isBeforeFirst()) {
                            while (result.next()) {
                                totalTime += result.getLong("sessiontime");
                            }

                            Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(target);
                            if (!userOptional.isPresent()) return;

                            String s = Utils.formatPlaytime(totalTime);
                            p.sendMessage(Utils.f(userOptional.get().getColoredName() + " &7has played for &a" + s + " &7in the last week."));
                        } else {
                            p.sendMessage(Utils.f("&cThe player " + target + " either doesn't exist, or hasn't played in the last week."));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
