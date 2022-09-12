package net.grandtheftmc.core.database.dao;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.alert.Alert;
import net.grandtheftmc.core.alert.AlertEntry;
import net.grandtheftmc.core.alert.AlertManager;
import net.grandtheftmc.core.alert.type.AlertShowType;
import net.grandtheftmc.core.alert.type.AlertType;
import net.grandtheftmc.core.database.BaseDatabase;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AlertsDAO {

    public static boolean createAlertsTable() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `alerts`(" +
                    "`id` int NOT NULL AUTO_INCREMENT," +
                    "`name` varchar(255) NOT NULL," +
                    "`description` varchar(255) DEFAULT 'none'," +
                    "`image` varchar(255)," +
                    "`link` varchar(255)," +
                    "`showType` varchar(32) NOT NULL," +
                    "`type` varchar(32) NOT NULL," +
                    "`disabled` varchar(6)," +
                    "`start` timestamp NOT NULL," +
                    "`end` timestamp NOT NULL," +
                    "`player` varchar(32) NOT NULL," +
                    "`addon` longtext," +
                    "PRIMARY KEY (`id`));")) {
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:createAlertsTable()] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean createAlertUserTable() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `alert_users`(" +
                    "`uuid` varchar(36) NOT NULL," +
                    "`id` int NOT NULL," +
                    "`complete` varchar(6) NOT NULL," +
                    "`input` longtext);")) {
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:createAlertUserTable()] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Optional<List<Alert>[]> fetchAllAlerts() {
        List<Alert>[] alertList = new ArrayList[2];//0=alerts, 1=polls
        alertList[0] = Lists.newArrayList();
        alertList[1] = Lists.newArrayList();

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `alerts`;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        int id = result.getInt("id");
                        String name = result.getString("name");
                        String player = result.getString("player");
                        AlertType type;
                        AlertShowType showType;
                        try {
                            type = AlertType.valueOf(result.getString("type"));
                            showType = AlertShowType.valueOf(result.getString("showType"));
                        } catch (Exception e) {
                            continue;
                        }
                        String link = result.getString("link"), desc = result.getString("description");
                        Timestamp start = result.getTimestamp("start"), end = result.getTimestamp("end");
                        boolean disabled = Boolean.parseBoolean(result.getString("disabled"));

                        if (type == AlertType.POLL) {
                            AlertEntry entry = new AlertEntry(name, null, showType, type, link, start, end, disabled);
                            entry.setUniqueIdentifier(id);
                            entry.setDescription(desc);
                            entry.setPlayer(player);
                            alertList[1].add(entry);
                            continue;
                        }

                        String image = result.getString("image");
                        AlertEntry entry = new AlertEntry(name, image, showType, type, link, start, end, disabled);
                        entry.setUniqueIdentifier(id);
                        entry.setDescription(desc);
                        entry.setPlayer(player);
                        alertList[0].add(entry);
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:createAlertUserTable()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(alertList);
    }

    public static boolean insertAlert(Alert alert) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `alerts` (`name`,`description`,`image`,`link`,`showType`,`type`,`disabled`,`start`,`end`,`player`,`addon`) VALUES(?,?,?,?,?,?,?,?,?,?,?);")) {
                statement.setString(1, alert.getName());
                statement.setString(2, alert.getDescription());
                statement.setString(3, alert.getImageUrl());
                statement.setString(4, alert.getLink());
                statement.setString(5, alert.getShowType().name());
                statement.setString(6, alert.getAlertType().name());
                statement.setString(7, String.valueOf(alert.isDisabled()));
                statement.setTimestamp(8, alert.getStart());
                statement.setTimestamp(9, alert.getEnd());
                statement.setString(10, alert.getPlayer());
                statement.setString(11, "views:0");
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:insertAlert(alert)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean insertAlertUser(Player player, Alert alert) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `alert_users` (`uuid`,`id`,`complete`,`input`) VALUES(?,?,?,?);")) {
                statement.setString(1, player.getUniqueId().toString());
                statement.setInt(2, alert.getUniqueIdentifier());
                statement.setString(3, "true");
                statement.setString(4, "none");
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:insertAlertUser(player,alert)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateAlert(Alert alert) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `alerts` SET `name`=?,`description`=?,`image`=?,`link`=?,`showType`=?,`type`=?,`disabled`=?,`start`=?,`end`=?,`player`=?,`addon`=? WHERE `id`=?;")) {
                statement.setString(1, alert.getName());
                statement.setString(2, alert.getDescription());
                statement.setString(3, alert.getImageUrl());
                statement.setString(4, alert.getLink());
                statement.setString(5, alert.getShowType().name());
                statement.setString(6, alert.getAlertType().name());
                statement.setString(7, String.valueOf(alert.isDisabled()));
                statement.setTimestamp(8, alert.getStart());
                statement.setTimestamp(9, alert.getEnd());
                statement.setString(10, alert.getPlayer());
                statement.setString(11, "views:0");
                statement.setInt(12, alert.getUniqueIdentifier());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:updateAlert(alert)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deleteAlert(Alert alert) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `alerts` WHERE `id`=?;")) {
                statement.setInt(1, alert.getUniqueIdentifier());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:deleteAlert(alert)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Optional<List<Alert>> fetchAlertsForPlayer(AlertManager alertManager, UUID uuid) {
        List<Alert> list = alertManager.getAvailableAlerts();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `id` FROM `alert_users` WHERE `uuid`=?;")) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    while(result.next()) {
                        int id = result.getInt("id");
                        alertManager.getAvailableAlertById(id).ifPresent(list::remove);
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[AlertsDAO:fetchAlertsForPlayer(uuid)] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(list);
    }
}
