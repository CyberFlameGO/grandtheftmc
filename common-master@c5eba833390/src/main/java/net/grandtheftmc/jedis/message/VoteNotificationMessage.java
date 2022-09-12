package net.grandtheftmc.jedis.message;

import java.util.UUID;

import net.grandtheftmc.jedis.JMessage;

public class VoteNotificationMessage implements JMessage {

	/** The uuid of the user the vote notification is for */
    private final UUID uuid;
    /** The message to pass along */
    private final String message;

    /**
     * Construct a new VoteNotificationMessage.
     * <p>
     * This is forwarded to all servers to let the user know that we have received their vote.
     * </p>
     * 
     * @param uuid - the uuid of the user to notify
     * @param message - the message to pass along
     */
    public VoteNotificationMessage(UUID uuid, String message) {
        this.uuid = uuid;
        this.message = message;
    }

    /**
     * Get the UUID of the user that the vote notification is for.
     * 
     * @return The UUID of the user that the vote notification is for.
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Get the message that should be passed along to this user.
     * 
     * @return The message that should be displayed to the user.
     */
	public String getMessage() {
		return message;
	}
}

