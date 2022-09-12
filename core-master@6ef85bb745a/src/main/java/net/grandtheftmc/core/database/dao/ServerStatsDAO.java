package net.grandtheftmc.core.database.dao;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class ServerStatsDAO {

    public static boolean reset(boolean daily) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(!daily ? "UPDATE `server_stats` SET `playedServers`=NULL,`weeklyLoginTime`=?;" : "UPDATE `server_stats` SET `dailyPlayTime`=0,`dailyLoginTime`=?;")) {
                statement.setLong(1, System.currentTimeMillis());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[ServerStatsDAO:reset(daily)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Optional<WeeklyStatsData> fetchWeeklyStats() {
        WeeklyStatsData data = new WeeklyStatsData();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `server_stats`;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        data.totalLogins += 1;
                        long firstLogin = result.getLong("firstLogin");
                        long weeklyLogin = result.getLong("weeklyLoginTime");
                        if ((System.currentTimeMillis() - firstLogin) <= (1000 * 60 * 60 * 24 * 7)) {
                            data.totalNewPlayers += 1;
                            if (weeklyLogin != 0 && weeklyLogin != firstLogin) {
                                data.totalNewPlayersLoginAgain += 1;
                            }
                        }

                        String playedServers = result.getString("playedServers") == null ? result.getString("playedServers") : "";
                        if (playedServers.contains("vice") && playedServers.contains("gtm")) {
                            data.totalPlayersPlayedBoth += 1;
                            continue;
                        }

                        if (playedServers.contains("vice"))
                            data.totalPlayersPlayedVice += 1;

                        if (playedServers.contains("gtm"))
                            data.totalPlayersPlayedGTM += 1;
                    }

                    data.setData(new Date().toGMTString() + "-" + data.totalNewPlayersLoginAgain + "-" + data.totalPlayersPlayedBoth + "-" + data.totalPlayersPlayedVice + "-" + data.totalPlayersPlayedGTM + "-" + data.totalNewPlayers + "-" + data.totalLogins);
                }
            }
        } catch (SQLException e) {
            Core.error("[ServerStatsDAO:fetchWeeklyStats] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(data);
    }

    public static Optional<DailyStatsData> fetchDailyStats() {
        DailyStatsData data = new DailyStatsData();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `server_stats`;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        String uuid = result.getString("uuid");

                        if((System.currentTimeMillis() - result.getLong("firstLogin")) <= 1000 * 60 * 60 * 24)
                            data.dailyNewPlayers++;

                        if((System.currentTimeMillis() - result.getLong("dailyLoginTime")) <= 1000 * 60 * 60 * 24) {
                            data.dailyTotalLogins++;
                            long playtime = result.getLong("dailyPlayTime");
                            data.dailyPlaytime += playtime;

                            UserRank rank = UserDAO.fetchUserRank(UUID.fromString(uuid));
                            if(rank == UserRank.DEFAULT) {
                                data.dailyLoginsDefault++;
                                data.dailyPlaytimeDefault += playtime;
                            }
                            else {
                                data.dailyLoginsRanked++;
                                data.dailyPlaytimeRanked += playtime;
                            }
                        }
                    }


                }
            }
        } catch (SQLException e) {
            Core.error("[ServerStatsDAO:fetchDailyStats] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(data);
    }

    public static void updatePlaytimeAndFirstlogin(Connection connection, Player player, User user) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `server_stats` set `dailyPlayTime`=?,`firstLogin`=? WHERE `uuid`=?;")) {
            statement.setLong(1, System.currentTimeMillis() - user.getLoginTime() + user.getDailyPlayTime());
            statement.setLong(2, player.getFirstPlayed());
            statement.setString(3, player.getUniqueId().toString());
            statement.execute();
        } catch (SQLException e) {
            Core.error("[ServerStatsDAO:updatePlaytimeAndFirstlogin(player,user)] SQLException occurred");
            e.printStackTrace();
        }
    }



    public static class WeeklyStatsData {
        private String data;
        private double totalLogins = 0, totalNewPlayers = 0, totalNewPlayersLoginAgain = 0, totalPlayersPlayedBoth = 0, totalPlayersPlayedVice = 0, totalPlayersPlayedGTM = 0;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public double getTotalLogins() {
            return totalLogins;
        }

        public double getTotalNewPlayers() {
            return totalNewPlayers;
        }

        public double getTotalNewPlayersLoginAgain() {
            return totalNewPlayersLoginAgain;
        }

        public double getTotalPlayersPlayedBoth() {
            return totalPlayersPlayedBoth;
        }

        public double getTotalPlayersPlayedVice() {
            return totalPlayersPlayedVice;
        }

        public double getTotalPlayersPlayedGTM() {
            return totalPlayersPlayedGTM;
        }
    }

    public static class DailyStatsData {
        private String data;
        private double dailyTotalLogins = 0, dailyPlaytime = 0, dailyNewPlayers = 0, dailyPlaytimeRanked = 0, dailyLoginsRanked = 0, dailyLoginsDefault = 0, dailyPlaytimeDefault = 0;

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public double getDailyTotalLogins() {
            return dailyTotalLogins;
        }

        public void setDailyTotalLogins(double dailyTotalLogins) {
            this.dailyTotalLogins = dailyTotalLogins;
        }

        public void setDailyPlaytime(double dailyPlaytime) {
            this.dailyPlaytime = dailyPlaytime;
        }

        public void setDailyNewPlayers(double dailyNewPlayers) {
            this.dailyNewPlayers = dailyNewPlayers;
        }

        public void setDailyPlaytimeRanked(double dailyPlaytimeRanked) {
            this.dailyPlaytimeRanked = dailyPlaytimeRanked;
        }

        public void setDailyLoginsRanked(double dailyLoginsRanked) {
            this.dailyLoginsRanked = dailyLoginsRanked;
        }

        public void setDailyLoginsDefault(double dailyLoginsDefault) {
            this.dailyLoginsDefault = dailyLoginsDefault;
        }

        public void setDailyPlaytimeDefault(double dailyPlaytimeDefault) {
            this.dailyPlaytimeDefault = dailyPlaytimeDefault;
        }

        public double getDailyPlaytime() {
            return dailyPlaytime;
        }

        public double getDailyNewPlayers() {
            return dailyNewPlayers;
        }

        public double getDailyPlaytimeRanked() {
            return dailyPlaytimeRanked;
        }

        public double getDailyLoginsRanked() {
            return dailyLoginsRanked;
        }

        public double getDailyLoginsDefault() {
            return dailyLoginsDefault;
        }

        public double getDailyPlaytimeDefault() {
            return dailyPlaytimeDefault;
        }
    }
}
