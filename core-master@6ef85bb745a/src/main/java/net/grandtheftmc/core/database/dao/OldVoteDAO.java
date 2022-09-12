package net.grandtheftmc.core.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.UserRank;

public class OldVoteDAO {

    public static Optional<List<String>> fetchRelevantVoters() {
        List<String> list = Lists.newArrayList();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `uuid` FROM `votes` WHERE `monthlyVotes`>0 ORDER BY `monthlyVotes` DESC;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        list.add(result.getString("uuid"));
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[VoteDAO:getRelevantVoters()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(list);
    }

    public static boolean updateMonthlyVotes(UUID uuid, int amount) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `votes` SET `monthlyVotes`=? WHERE `uuid`=?;")) {
                statement.setInt(1, amount);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateMonthlyVotes(uuid,amount)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateTotalVotes(UUID uuid, int amount) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `votes` SET `totalVotes`=? WHERE `uuid`=?;")) {
                statement.setInt(1, amount);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateTotalVotes(uuid,amount)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateLastVote(UUID uuid, String voteSite, long timestamp) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `votes` SET " + voteSite + "=? WHERE `uuid`=?;")) {
                statement.setLong(1, timestamp);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateLastVote(uuid,voteSite,timestamp)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserVotes(UUID uuid, int amount) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `votes`=? WHERE `uuid`=?;")) {
                statement.setInt(1, amount);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserVotes(uuid,amount)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserVotesByName(String name, int amount) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `votes`=? WHERE `lastname`=?;")) {
                statement.setInt(1, amount);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserVotes(uuid,amount)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserVoteStreak(UUID uuid, int voteStreak) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `voteStreak`=? WHERE `uuid`=?;")) {
                statement.setInt(1, voteStreak);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserVoteStreak(uuid,voteStreak)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserLastVoteStreak(UUID uuid, long lastVoteStreak) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `lastVoteStreak`=? WHERE `uuid`=?;")) {
                statement.setLong(1, lastVoteStreak);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserLastVoteStreak(uuid,lastVoteStreak)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserLastVoteStreakByName(String name, long lastVoteStreak) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `lastVoteStreak`=? WHERE `lastname`=?;")) {
                statement.setLong(1, lastVoteStreak);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserLastVoteStreakByName(name,lastVoteStreak)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserDailyStreak(UUID uuid, int dailyStreak) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `dailyStreak`=? WHERE `uuid`=?;")) {
                statement.setInt(1, dailyStreak);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserDailyStreak(uuid,dailyStreak)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserDailyStreakByName(String name, int dailyStreak) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `dailyStreak`=? WHERE `lastname`=?;")) {
                statement.setInt(1, dailyStreak);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserDailyStreakByName(name,dailyStreak)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserDaily(UUID uuid, int dailyStreak, long lastDailyReward) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `dailyStreak`=?, `lastDailyReward`=? WHERE `uuid`=?;")) {
                statement.setInt(1, dailyStreak);
                statement.setLong(2, lastDailyReward);
                statement.setString(3, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserDaily(uuid,dailyStreak,lastDailyReward)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserLastDailyReward(UUID uuid, long lastDailyReward) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `lastDailyReward`=? WHERE `uuid`=?;")) {
                statement.setLong(1, lastDailyReward);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserLastDailyReward(uuid,lastDailyReward)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserLastDailyRewardByName(String name, long lastDailyReward) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `lastDailyReward`=? WHERE `lastname`=?;")) {
                statement.setLong(1, lastDailyReward);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserLastDailyRewardByName(name,lastDailyReward)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean deleteLastMonthsVoters() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `last_months_voters`;")) {
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:deleteLastMonthsVoters()] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Optional<VoteUser[]> getTopTenVoters() {
        VoteUser[] voteUsers = new VoteUser[10];
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT U.name AS voter_name, HEX(LUV.uuid), COUNT(*) AS total_votes FROM log_user_vote LUV, user U WHERE U.uuid=LUV.uuid AND YEAR(LUV.creation) = YEAR(CURRENT_DATE()) AND MONTH(LUV.creation) = MONTH(CURRENT_DATE()) GROUP by LUV.uuid ORDER BY COUNT(*) DESC LIMIT 10;")) {
                try (ResultSet result = statement.executeQuery()) {
                    int i = 0;
                    while(result.next()) {
                        voteUsers[i] = new VoteUser(i + 1, result.getInt("total_votes"), result.getString("voter_name"));
                        i += 1;
                    }

                    return Optional.of(voteUsers);
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:getTopTenVoters()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<VoteUser[]> getLastMonthsVoters() {
        VoteUser[] voteUsers = new VoteUser[10];
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `last_months_voters` ORDER BY `slot` DESC LIMIT 10;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while(result.next()) {
                        int slot = result.getInt("slot");
                        voteUsers[slot - 1] = new VoteUser(slot, result.getString("name"));
                    }

                    return Optional.of(voteUsers);
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:getLastMonthsVoters()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<VoteUser> getTopVoter() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `votes` ORDER BY `votes`.`monthlyVotes` DESC LIMIT 1;")) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                         return Optional.of(new VoteUser(1, result.getInt("monthlyVotes"), result.getString("name")));
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:getTopVoter()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.empty();
    }

    public static boolean updateVoteStreakByName(String name, int streak) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `voteStreak`=? WHERE `lastname`=?;")) {
                statement.setInt(1, streak);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateVoteStreakByName(name,streak)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Optional<VoteUser> fetchRandomVoteStreaker(int minStreak) {
        //"SELECT * FROM users WHERE voteStreak >20 ORDER BY RAND() LIMIT 1;"
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE `voteStreak`>? ORDER BY RAND() LIMIT 1;")) {
                statement.setInt(1, minStreak);
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        return Optional.of(new VoteUser(UUID.fromString(result.getString("uuid")),
                                result.getString("name"),
                                result.getInt("voteStreak"),
                                UserRank.getUserRank(result.getString("userrank"))));
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchRandomVoteStreaker(minStreak)] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.empty();
    }
    
    /**
     * Logs a user vote into the database for storage purposes.
     * 
     * @param conn - the database connection thread
     * @param voter - the uuid of the voter
     * @param amount - the amount their vote is worth, as some can have higher values
     * @param serviceID - the id of the service that they voted on
     * 
     * @return {@code true} if the user vote was logged, {@code false} otherwise.
     */
    public static boolean logUserVote(Connection conn, UUID voter, int amount, int serviceID){
    	
    	String query = "INSERT INTO log_user_vote (uuid, amount, service_id) VALUES (UNHEX(?), ?, ?);";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, voter.toString().replace("-", ""));
    		ps.setInt(2, amount);
    		ps.setInt(3, serviceID);
    		
    		ps.executeUpdate();
    	}
    	catch(SQLException e){
    		Core.log("[VoteDAO] Error logging vote for voter uuid=" + voter.toString() + ", amount=" + amount + ", serviceID=" + serviceID);
    		e.printStackTrace();
    		return false;
    	}
    	
    	return true;
    }

    public static class VoteUser {
        private int possition, votes;
        private String name;

        private UUID uuid;
        private int streak;
        private UserRank rank;

        public VoteUser(int possition, int votes, String name) {
            this.possition = possition;
            this.votes = votes;
            this.name = name;
        }

        public VoteUser(int possition, String name) {
            this.possition = possition;
            this.name = name;
        }

        public VoteUser(UUID uuid, String name, int streak, UserRank rank) {
            this.uuid = uuid;
            this.name = name;
            this.streak = streak;
            this.rank = rank;
        }

        public int getPossition() {
            return possition;
        }

        public int getVotes() {
            return votes;
        }

        public String getName() {
            return name;
        }

        public UUID getUuid() {
            return uuid;
        }

        public int getStreak() {
            return streak;
        }

        public UserRank getRank() {
            return rank;
        }
    }
}
