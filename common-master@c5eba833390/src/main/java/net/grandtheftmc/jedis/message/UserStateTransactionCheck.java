package net.grandtheftmc.jedis.message;

import java.util.UUID;

import net.grandtheftmc.jedis.JMessage;

public class UserStateTransactionCheck implements JMessage {

	/** The uuid of the user to check transactions for */
	private final UUID uuid;

	/**
	 * Construct a new UserStateTransactionCheck.
	 * <p>
	 * This is forwarded to all servers and to force check for user state
	 * transactions for the given user.
	 * </p>
	 * 
	 * @param uuid - the uuid of the user to check transactions for
	 */
	public UserStateTransactionCheck(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Get the UUID of the user to check the user state transactions for.
	 * 
	 * @return The UUID of the user to check the user state transactions in the
	 *         database.
	 */
	public UUID getUUID() {
		return uuid;
	}
}
