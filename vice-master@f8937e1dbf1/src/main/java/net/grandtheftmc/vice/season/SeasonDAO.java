package net.grandtheftmc.vice.season;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SeasonDAO {

//CREATE TABLE IF NOT EXISTS season (
//season_num INT NOT NULL,
//server_key VARCHAR(8) NOT NULL,
//start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
//expire_time TIMESTAMP NOT NULL,
//data BLOB DEFAULT NULL,
//PRIMARY KEY (id, server_key)
//);

    public static List<Season> getSeasons(Connection connection) {
        List<Season> list = Lists.newArrayList();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM season WHERE server_key=?;")) {
            statement.setString(1, Core.getSettings().getType().name());
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    int id = result.getInt("season_num");
                    String data = result.getString("data");
                    Season season = new Season(
                            id,
                            result.getTimestamp("start_time"),
                            result.getTimestamp("expire_time"),
                            data == null || data.equals("NULL") ? null : new SeasonData(data),
                            SeasonManager.SEASON == id
                    );
                    list.add(season);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
