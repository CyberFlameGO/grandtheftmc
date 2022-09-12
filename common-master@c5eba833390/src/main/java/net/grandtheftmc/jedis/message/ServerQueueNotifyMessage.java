package net.grandtheftmc.jedis.message;

import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.jedis.JMessage;

import java.util.UUID;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public class ServerQueueNotifyMessage implements JMessage {

    private final UUID uniqueId;
    private final ServerTypeId targetServer;
    private final int possition;

    public ServerQueueNotifyMessage(UUID uniqueId, ServerTypeId targetServer, int possition) {
        this.uniqueId = uniqueId;
        this.targetServer = targetServer;
        this.possition = possition;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public ServerTypeId getTargetServer() {
        return targetServer;
    }

    public int getPossition() {
        return possition;
    }
}
