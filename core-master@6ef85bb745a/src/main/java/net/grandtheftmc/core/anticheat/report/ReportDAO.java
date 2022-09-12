package net.grandtheftmc.core.anticheat.report;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.servers.ServerType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportDAO {

    /**
     * A {@link Pattern} used to identify and/or split full UUIDs
     */
    public static final Pattern PATTERN_UUID = Pattern.compile("^[a-z0-9]{8}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{12}$", Pattern.CASE_INSENSITIVE);
    /**
     * A {@link Pattern} used to identify and/or split trimmed UUIDs
     */
    public static final Pattern PATTERN_TRIMMED_UUID = Pattern.compile("^([a-z0-9]{8})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{4})([a-z0-9]{12})$", Pattern.CASE_INSENSITIVE);


    public static void createLogTable() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXIST `report_logs`(" +
                    "`reporter` BINARY(16) NOT NULL," +
                    "`reporterName` BINARY(16) NOT NULL," +
                    "`timeReported` BIGINT NOT NULL," +
                    "`reason` VARCHAR(32) NOT NULL," +
                    "`victim` BINARY(16) NOT NULL," +
                    "`victimName` BINARY(16) NOT NULL," +
                    "`serverType` VARCHAR(10) NOT NULL," +
                    "`serverId` INT NOT NULL," +
                    "INDEX(`reporterName`,`victim`,`victimName`,`serverType`,`serverId`)" +
                    ");")) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertReport(UUID reporterUniqueId, UUID victimUniqueId, String reporterName, String victimName, String reason, long timeReported, ServerType serverType, int serverId) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `report_logs`(" +
                    "`reporter`,`reporterName`,`timeReported`,`reason`,`victim`,`victimName`,`serverType`,`serverId`) VALUES(?,?,?,?,?,?,?,?);")) {
                statement.setString(1, reporterUniqueId.toString().replaceAll("-", ""));
                statement.setString(2, reporterName);
                statement.setLong(3, timeReported);
                statement.setString(4, reason);
                statement.setString(5, victimUniqueId.toString().replaceAll("-", ""));
                statement.setString(6, victimName);
                statement.setString(6, serverType.name());
                statement.setInt(6, serverId);

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a UUID safely from a {@link String}.
     *
     * @param string The {@link String} to deserialize into an {@link UUID} object.
     * @return {@link Optional#empty()} if the provided {@link String} is illegal, otherwise an {@link Optional}
     * containing the deserialized {@link UUID} object.
     */
    private Optional<UUID> createUUID(String string) {
        if (string == null) {
            return Optional.empty();
        }

        UUID result = null;

        try {
            // Is it a valid UUID?
            if (!PATTERN_UUID.matcher(string).matches()) {
                // Un-trim UUID if it is trimmed
                Matcher matcher = PATTERN_TRIMMED_UUID.matcher(string);
                if (matcher.matches()) {
                    StringBuilder sb = new StringBuilder();

                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        if (i != 1) {
                            sb.append("-");
                        }

                        sb.append(matcher.group(i));
                    }

                    string = sb.toString();
                } else {
                    // Invalid UUID
                    string = null;
                }
            }

            if (string != null) {
                result = UUID.fromString(string);
            }
        } catch (IllegalArgumentException ignored) {
            // Useless data passed
        }

        return Optional.ofNullable(result);
    }
}
