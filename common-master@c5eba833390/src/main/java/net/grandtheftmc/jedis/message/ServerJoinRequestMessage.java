package net.grandtheftmc.jedis.message;

import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.jedis.JMessage;

import java.util.UUID;

/**
 * Created by Luke Bingham on 20/08/2017.
 */
public class ServerJoinRequestMessage implements JMessage {

    private final UUID uniqueId;
    private final ServerTypeId targetServer;

    public ServerJoinRequestMessage(UUID uniqueId, String rank, ServerTypeId targetServer) {
        this.uniqueId = uniqueId;
        this.targetServer = targetServer;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ServerTypeId getTargetServer() {
        return targetServer;
    }
}
