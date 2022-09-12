package net.grandtheftmc.jedis.message;

import net.grandtheftmc.ServerType;
import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.jedis.JMessage;

import java.util.UUID;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public class ServerQueueMessage implements JMessage {

    private final UUID uniqueId;
    private final String rank;
    private final ServerTypeId targetServer;

    public ServerQueueMessage(UUID uniqueId, String rank, ServerTypeId targetServer) {
        this.uniqueId = uniqueId;
        this.rank = rank;
        this.targetServer = targetServer;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getRank() {
        return rank;
    }

    public ServerTypeId getTargetServer() {
        return targetServer;
    }
}
