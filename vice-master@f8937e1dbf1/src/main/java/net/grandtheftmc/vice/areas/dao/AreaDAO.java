package net.grandtheftmc.vice.areas.dao;

import com.google.common.collect.Sets;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.obj.Area;

import java.sql.*;
import java.util.Set;

public class AreaDAO {

    /*
        Create:
        CREATE TABLE IF NOT EXISTS area (id INT NOT NULL AUTO_INCREMENT,season INT NOT NULL,name VARCHAR(36) NOT NULL,world VARCHAR(16) NOT NULL,min_x INT NOT NULL,max_x INT NOT NULL,min_z INT NOT NULL,max_z INT NOT NULL,PRIMARY KEY (id));
     */

    /**
     * Insert the area in to the database
     * Updates the Area ID upon completion
     * @param area
     * @return
     */
    public static boolean insert(Area area) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `area` (`season`,`name`,`world`,`min_x`,`max_x`,`min_z`,`max_z`) VALUES(?,?,?,?,?,?,?);", Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, area.getSeason());
                statement.setString(2, area.getName());
                statement.setString(3, area.getWorld().getName());
                statement.setInt(4, area.getMinX());
                statement.setInt(5, area.getMaxX());
                statement.setInt(6, area.getMinZ());
                statement.setInt(7, area.getMaxZ());

                statement.executeUpdate();

                try (ResultSet result = statement.getGeneratedKeys()) {
                    if (result.next()) {
                        area.setID(result.getInt(1));
                        Vice.log("[AreaDAO] Updated area '" + area.getName() + "' ID to " + area.getID());

                        return true;
                    }

                    Vice.error("[AreaDAO] Failed to find generated ID in 'insert' on Area: " + area.getName());
                    return false;
                }
            }
        } catch (SQLException e) {
            Vice.error("[AreaDAO] Failed to execute 'insert' on Area: " + area.getName());
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Delete the area by ID from the database
     * @param id
     * @return
     */
    public static boolean delete(int id) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `area` WHERE `id`=?;")) {
                statement.setInt(1, id);

                statement.execute();

                return true;
            }
        } catch (SQLException e) {
            Vice.error("[AreaDAO] Failed to execute 'delete' on Area: " + id);
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Update the name of the given area by ID in the database to the newly given name
     * @param id
     * @param newName
     * @return
     */
    public static boolean updateName(int id, String newName) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `area` SET `name`=? WHERE `id`=?;")) {
                statement.setString(1, newName);
                statement.setInt(2, id);

                statement.execute();

                return true;
            }
        } catch (SQLException e) {
            Vice.error("[AreaDAO] Failed to execute 'updateName' on Area: " + id);
            e.printStackTrace();

            return false;
        }
    }

    /**
     * Loads all seasons, regardless of season
     * @return
     */
    public static Set<Area> loadAll() {
        Set<Area> areas = Sets.newHashSet();

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `area`;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        int id = result.getInt("id");
                        int season = result.getInt("season");
                        String name = result.getString("name");
                        String world = result.getString("world");
                        int x1 = result.getInt("min_x");
                        int x2 = result.getInt("max_x");
                        int z1 = result.getInt("min_z");
                        int z2 = result.getInt("max_z");

                        areas.add(new Area(id, season, name, world, x1, x2, z1, z2));
                    }

                    Vice.log("Loaded " + areas.size() + " Areas");
                    return areas;
                }
            }
        } catch (SQLException e) {
            Vice.error("[AreaDAO] Failed to execute 'loadAll'");
            e.printStackTrace();

            return areas;
        }
    }

    /**
     * Loads all areas for a specific season
     * @param season
     * @return
     */
    public static Set<Area> loadBySeason(int season) {
        Set<Area> areas = Sets.newHashSet();

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `area` WHERE `season`=?;")) {
                statement.setInt(1, season);

                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        int id = result.getInt("id");
                        String name = result.getString("name");
                        String world = result.getString("world");
                        int x1 = result.getInt("min_x");
                        int x2 = result.getInt("max_x");
                        int z1 = result.getInt("min_z");
                        int z2 = result.getInt("max_z");

                        areas.add(new Area(id, season, name, world, x1, x2, z1, z2));
                    }

                    Vice.log("Loaded " + areas.size() + " Areas");
                    return areas;
                }
            }
        } catch (SQLException e) {
            Vice.error("[AreaDAO] Failed to execute 'loadBySeason(" + season + ")'");
            e.printStackTrace();

            return areas;
        }
    }

}
