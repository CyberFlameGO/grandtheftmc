package net.grandtheftmc.core.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.debug.Log;
import net.grandtheftmc.core.voting.VoteRecord;
import net.grandtheftmc.core.voting.VoteSite;

public class VoteDAO {

	/**
	 * Creates an empty user vote record in the database.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to create the record for
	 * 
	 * @return {@code true} if the record was created, {@code false} otherwise.
	 */
	public static boolean createUserVoteRecord(Connection conn, UUID uuid) {

		String query = "INSERT IGNORE INTO user_vote (uuid) VALUES (UNHEX(?));";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));

			ps.executeUpdate();
		}
		catch (SQLException exc) {
			exc.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Obtain a read only version of the user's vote record.
	 * <p>
	 * Note: If the vote record does not exist, create a new empty one for them.
	 * </p>
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to get their vote record
	 * 
	 * @return The VoteRecord for the specified user.
	 */
	public static VoteRecord getUserVoteRecord(Connection conn, UUID uuid) {

		String query = "SELECT streak, total_votes, max_streak, last_vote, site_one, site_two, site_three, site_four, site_five FROM user_vote WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));

			try (ResultSet result = ps.executeQuery()) {

				// if vote record
				if (result.next()) {

					int streak = result.getInt("streak");
					int totalVotes = result.getInt("total_votes");
					int maxStreak = result.getInt("max_streak");
					Timestamp lastVote = result.getTimestamp("last_vote");
					Timestamp siteOne = result.getTimestamp("site_one");
					Timestamp siteTwo = result.getTimestamp("site_two");
					Timestamp siteThree = result.getTimestamp("site_three");
					Timestamp siteFour = result.getTimestamp("site_four");
					Timestamp siteFive = result.getTimestamp("site_five");

					VoteRecord vr = new VoteRecord(uuid, totalVotes, streak, maxStreak, lastVote);
					vr.setVoteTimestamp(VoteSite.ONE, siteOne);
					vr.setVoteTimestamp(VoteSite.TWO, siteTwo);
					vr.setVoteTimestamp(VoteSite.THREE, siteThree);
					vr.setVoteTimestamp(VoteSite.FOUR, siteFour);
					vr.setVoteTimestamp(VoteSite.FIVE, siteFive);

					return vr;
				}
				else {

					// create an empty record for them
					createUserVoteRecord(conn, uuid);
				}
			}
		}
		catch (SQLException exc) {
			exc.printStackTrace();
		}

		return new VoteRecord(uuid);
	}

	/**
	 * Updates the player's total votes, incrementing it by one.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to increment their total votes
	 * 
	 * @return {@code true} if their total votes was successfully updated,
	 *         {@code false} otherwise.
	 */
	public static boolean incrementTotalVotes(Connection conn, UUID uuid) {

		String query = "UPDATE user_vote SET total_votes=total_votes+1 WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			exc.printStackTrace();
		}

		return false;
	}

	/**
	 * Updates the player's vote streak, incrementing it by one.
	 * <p>
	 * Note: This also updates their last vote timestamp.
	 * </p>
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to increment their vote streak
	 * 
	 * @return {@code true} if their vote streak was successfully updated,
	 *         {@code false} otherwise.
	 */
	public static boolean incrementVoteStreak(Connection conn, UUID uuid) {

		String query = "UPDATE user_vote SET streak=streak+1, last_vote=CURRENT_TIMESTAMP WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			exc.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Updates the player's vote streak to the specified amount.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to update
	 * @param voteStreak - the vote streak amount to set it to
	 * 
	 * @return {@code true} if their vote streak was successfully updated,
	 *         {@code false} otherwise.
	 */
	public static boolean setVoteStreak(Connection conn, UUID uuid, int voteStreak) {

		String query = "UPDATE user_vote SET streak=? WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, voteStreak);
			ps.setString(2, uuid.toString().replaceAll("-", ""));

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			exc.printStackTrace();
		}

		return false;
	}

	/**
	 * Resets the vote streak of the specified user.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to reset their vote streak
	 * 
	 * @return {@code true} if the vote streak was reset, {@code false}
	 *         otherwise.
	 */
	public static boolean resetVoteStreak(Connection conn, UUID uuid) {

		String query = "UPDATE user_vote SET streak=0 WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			exc.printStackTrace();
		}

		return false;
	}

	/**
	 * Updates the max streak that this user has.
	 * 
	 * @param conn - the database connection
	 * @param uuid - the uuid of the user
	 * @param maxStreak - the max streak that this user has
	 * 
	 * @return {@code true} if the query ran, {@code false} otherwise.
	 */
	public static boolean updateMaxStreak(Connection conn, UUID uuid, int maxStreak) {

		String query = "UPDATE user_vote SET max_streak=? WHERE uuid=UNHEX(?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, maxStreak);
			ps.setString(2, uuid.toString().replaceAll("-", ""));

			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			exc.printStackTrace();
		}

		return false;

	}

	/**
	 * Update the vote site timestamp for the specified uuid.
	 * 
	 * @param conn - the database connection
	 * @param uuid - the uuid of the user
	 * @param voteSite - the vote site to update
	 * 
	 * @return {@code true} if the query ran, {@code false} otherwise.
	 */
	public static boolean updateVoteSiteTimestamp(Connection conn, UUID uuid, VoteSite voteSite) {

		String query = null;

		switch (voteSite) {
			case ONE:
				query = "UPDATE user_vote SET site_one=CURRENT_TIMESTAMP WHERE uuid=UNHEX(?);";
				break;
			case TWO:
				query = "UPDATE user_vote SET site_two=CURRENT_TIMESTAMP WHERE uuid=UNHEX(?);";
				break;
			case THREE:
				query = "UPDATE user_vote SET site_three=CURRENT_TIMESTAMP WHERE uuid=UNHEX(?);";
				break;
			case FOUR:
				query = "UPDATE user_vote SET site_four=CURRENT_TIMESTAMP WHERE uuid=UNHEX(?);";
				break;
			case FIVE:
				query = "UPDATE user_vote SET site_five=CURRENT_TIMESTAMP WHERE uuid=UNHEX(?);";
				break;
			default:
				Log.error("VoteDAO", "Unhandled vote site when updated timestamp, this could duplicate votes! Fix immediately.");
				return false;
		}

		if (query != null){
			try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setString(1, uuid.toString().replaceAll("-", ""));

				ps.executeUpdate();
				return true;
			}
			catch (SQLException exc) {
				exc.printStackTrace();
			}
		}

		return false;

	}

	/**
	 * Logs a user vote into the database for storage purposes.
	 * 
	 * @param conn - the database connection thread
	 * @param voter - the uuid of the voter
	 * @param amount - the amount their vote is worth, as some can have higher
	 *            values
	 * @param serviceID - the id of the service that they voted on
	 * 
	 * @return {@code true} if the user vote was logged, {@code false}
	 *         otherwise.
	 */
	public static boolean logUserVote(Connection conn, UUID voter, int amount, int serviceID) {

		String query = "INSERT INTO log_user_vote (uuid, amount, service_id) VALUES (UNHEX(?), ?, ?);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, voter.toString().replace("-", ""));
			ps.setInt(2, amount);
			ps.setInt(3, serviceID);

			ps.executeUpdate();
		}
		catch (SQLException e) {
			Core.log("[VoteDAO] Error logging vote for voter uuid=" + voter.toString() + ", amount=" + amount + ", serviceID=" + serviceID);
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	/**
	 * Get the top voters for this month.
	 * <p>
	 * If the specified amount is 10, that is the top 10 voters.
	 * 
	 * Note: This mines the log_user_vote table and the result should be cached as it's a complicated query.
	 * </p>
	 * 
	 * @param conn - the database connection thread
	 * @param amount - the number of top voters to get
	 * 
	 * @return The vote records for the top amount voters.
	 */
	public static Optional<VoteUser[]> getTopVoters(Connection conn, int amount) {
        VoteUser[] voteUsers = new VoteUser[amount];
        
        String query = "SELECT U.name AS voter_name, HEX(LUV.uuid), COUNT(*) AS total_votes FROM log_user_vote LUV, user U WHERE U.uuid=LUV.uuid AND YEAR(LUV.creation) = YEAR(CURRENT_DATE()) AND MONTH(LUV.creation) = MONTH(CURRENT_DATE()) GROUP by LUV.uuid ORDER BY COUNT(*) DESC LIMIT ?;";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
        	ps.setInt(1, amount);
        	
            try (ResultSet result = ps.executeQuery()) {
                int i = 0;
                while(result.next()) {
                    voteUsers[i] = new VoteUser(i + 1, result.getInt("total_votes"), result.getString("voter_name"));
                    i += 1;
                }

                return Optional.of(voteUsers);
            }
        }
        catch (SQLException e) {
            Core.error("[UserDAO:getTopVoters()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }
    }
	
	/**
	 * Get the top voters for LAST month.
	 * <p>
	 * If the specified amount is 10, that is the top 10 voters.
	 * 
	 * Note: This mines the log_user_vote table and the result should be cached as it's a complicated query.
	 * </p>
	 * 
	 * @param conn - the database connection thread
	 * @param amount - the number of top voters to get
	 * 
	 * @return The vote records for the top amount voters for LAST month.
	 */
	public static Optional<VoteUser[]> getLastTopVoters(Connection conn, int amount) {
        VoteUser[] voteUsers = new VoteUser[amount];
        
        String query = "SELECT U.name AS voter_name, HEX(LUV.uuid), COUNT(*) AS total_votes FROM log_user_vote LUV, user U WHERE U.uuid=LUV.uuid AND YEAR(LUV.creation) = YEAR(CURRENT_DATE() - INTERVAL 1 MONTH) AND MONTH(LUV.creation) = MONTH(CURRENT_DATE() - INTERVAL 1 MONTH) GROUP by LUV.uuid ORDER BY COUNT(*) DESC LIMIT ?;";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
        	ps.setInt(1, amount);
        	
            try (ResultSet result = ps.executeQuery()) {
                int i = 0;
                while(result.next()) {
                    voteUsers[i] = new VoteUser(i + 1, result.getInt("total_votes"), result.getString("voter_name"));
                    i += 1;
                }

                return Optional.of(voteUsers);
            }
        }
        catch (SQLException e) {
            Core.error("[UserDAO:getLastTopVoters()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }
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
