package net.grandtheftmc.vice.areas.dao;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.vice.Vice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DiscoveryDAO {

    /**
     * Inserts the given values to the Discovery table
     */
    public static boolean insert(UUID uuid, int season, int area) {
        String id = uuid.toString().replaceAll("-", "");

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `discovery` (`uuid`,`season`,`area`) VALUES(UNHEX(?),?,?);")) {
                statement.setString(1, id);
                statement.setInt(2, season);
                statement.setInt(3, area);

                statement.execute();

                return true;
            }
        } catch (SQLException e) {
            Vice.error("[DiscoveryDAO] Failed to execute 'insert'");
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Removes all discoveries which match the given area ID
     * @param area
     * @return
     */
    public static boolean deleteByArea(int area) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `discovery` WHERE `area`=?;")) {
                statement.setInt(1, area);

                statement.execute();

                return true;
            }
        } catch (SQLException e) {
            Vice.error("[DiscoveryDAO] Failed to execute 'deleteByArea'");
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Returns a Map containing all areas ever discovered by the given UUID
     * Map<Season #, Set<Area ID>>
     * @param uuid
     * @return
     */
    public static Map<Integer, Set<Integer>> getAllByUUID(UUID uuid) {
        Map<Integer, Set<Integer>> areas = Maps.newHashMap();
        String id = uuid.toString().replaceAll("-", "");

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `discovery` WHERE `uuid`=HEX(?);")) {
                statement.setString(1, id);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        int season = result.getInt("season");
                        int area = result.getInt("area");

                        if (areas.containsKey(season)) {
                            areas.get(season).add(area);
                        } else {
                            Set<Integer> newSet = Sets.newHashSet();
                            newSet.add(area);
                            areas.put(season, newSet);
                        }
                    }

                    return areas;
                }
            }
        } catch (SQLException e) {
            Vice.error("[DiscoveryDAO] Failed to execute 'getAllByUUID'");
            e.printStackTrace();

            return areas;
        }
    }

    /**
     * Returns a set containing all areas discovered by the given UUID during the given season
     * @param uuid
     * @param season
     * @return
     */
    public static Set<Integer> getSeasonByUUID(UUID uuid, int season) {
        Set<Integer> areas = Sets.newHashSet();
        String id = uuid.toString().replaceAll("-", "");

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `discovery` WHERE `uuid`=UNHEX(?) AND `season`=?;")) {
                statement.setString(1, id);
                statement.setInt(2, season);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        int area = result.getInt("area");

                        areas.add(area);
                    }

                    return areas;
                }
            }
        } catch (SQLException e) {
            Vice.error("[DiscoveryDAO] Failed to execute 'getSeasonByUUID'");
            e.printStackTrace();

            return areas;
        }
    }

}
