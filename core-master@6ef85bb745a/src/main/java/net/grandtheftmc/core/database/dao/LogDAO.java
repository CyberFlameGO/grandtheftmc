package net.grandtheftmc.core.database.dao;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class LogDAO {

    public static boolean insertLog(UUID uuid, String name, String action, String type, String reward, double amount, double price) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `logs`(`uuid`,`name`,`action`,`type`,`reward`,`amount`,`price`,`server`) VALUES(?,?,?,?,?,?,?,?);")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, name);
                statement.setString(3, action);
                statement.setString(4, type);
                statement.setString(5, reward);
                statement.setDouble(6, amount);
                statement.setDouble(7, price);
                statement.setString(8, Core.name());

                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:insertLog()] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
