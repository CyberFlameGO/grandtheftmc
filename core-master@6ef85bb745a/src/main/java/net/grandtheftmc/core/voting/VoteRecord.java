package net.grandtheftmc.core.voting;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Note: This class is designed as a read only response from the database.
 */
public class VoteRecord {

	/** Owner of the vote record */
	private final UUID owner;
	/** The total amount of votes the user has */
	private final int totalVotes;
	/** Current streak they are on */
	private final int streak;
	/** Max streak they ever received */
	private final int maxStreak;
	/** Timestamp for when they last voted */
	private final Timestamp lastVoted;
	/** Maps VoteSite to when they last voted */
	private final Map<VoteSite, Timestamp> siteToLastVote;

	/**
	 * Create a new VoteRecord.
	 * <p>
	 * This holds all cumulative information about a player's votes, and is read
	 * from the database.
	 * 
	 * @param owner - the owner of the vote record
	 * @param totalVotes - the total votes for the player
	 * @param streak - the current streak of voting
	 * @param maxStreak - the max streak that this player has
	 * @param lastVoted - the timestamp when the player last voted
	 */
	public VoteRecord(UUID owner, int totalVotes, int streak, int maxStreak, Timestamp lastVoted) {
		this.owner = owner;
		this.totalVotes = totalVotes;
		this.streak = streak;
		this.maxStreak = maxStreak;
		this.lastVoted = lastVoted;
		this.siteToLastVote = new HashMap<>();
	}

	/**
	 * Create a new VoteRecord.
	 * <p>
	 * This holds all cumulative information about a player's votes, and is read
	 * from the database.
	 * <p>
	 * Note: This has all null values, as the player has never voted before.
	 * 
	 * @param owner - the owner of the vote record
	 */
	public VoteRecord(UUID owner) {
		this(owner, 0, 0, 0, null);
	}

	/**
	 * Get the UUID of the owner of this record.
	 * 
	 * @return The UUID of the owner of this vote record.
	 */
	public UUID getOwner() {
		return owner;
	}
	
	/**
	 * Get the total votes the user has.
	 * 
	 * @return The total number of votes the user has.
	 */
	public int getTotalVotes() {
		return totalVotes;
	}

	/**
	 * Get the voting streak that this record currently has.
	 * 
	 * @return The streak that this record currently has.
	 */
	public int getStreak() {
		return streak;
	}

	/**
	 * Get the max streak for this voting record.
	 * 
	 * @return The max streak for this voting record.
	 */
	public int getMaxStreak() {
		return maxStreak;
	}

	/**
	 * Get the timestamp of when this user last voted, in general.
	 * 
	 * @return The timestamp of when this user last voted, if one exists.
	 */
	public Optional<Timestamp> getLastVoted() {
		return Optional.ofNullable(lastVoted);
	}

	/**
	 * Get the timestamp of the specified last vote site.
	 * 
	 * @param voteSite - the site that was voted on
	 * 
	 * @return The Timestamp for the site that was last voted, if it exists,
	 *         otherwise {@code null}.
	 */
	public Timestamp getVoteTimestamp(VoteSite voteSite) {
		if (siteToLastVote.containsKey(voteSite)) {
			return siteToLastVote.get(voteSite);
		}

		return null;
	}

	/**
	 * Set the voting timestamp of the specified vote site and timestamp.
	 * 
	 * @param voteSite - the site that was voted on
	 * @param timestamp - the timestamp that it was voted
	 */
	public void setVoteTimestamp(VoteSite voteSite, Timestamp timestamp) {
		siteToLastVote.put(voteSite, timestamp);
	}

	/**
	 * Get the mapping of vote site to timestamps.
	 * 
	 * @return The mapping of each vote site to the last vote.
	 */
	public Map<VoteSite, Timestamp> getSiteTimestamps() {
		return siteToLastVote;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "VoteRecord [owner=" + getOwner().toString() + ", streak=" + getStreak() + ", maxStreak=" + getMaxStreak() + ", lastVoted=" + lastVoted + "]";
	}
}
