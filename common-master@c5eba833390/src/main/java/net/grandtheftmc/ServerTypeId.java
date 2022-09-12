package net.grandtheftmc;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public class ServerTypeId {
    private final ServerType serverType;
    private final int id;

    public ServerTypeId(ServerType serverType, int id) {
        this.serverType = serverType;
        this.id = id;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public int getId() {
        return id;
    }

    public boolean isOperator() {
        return serverType == ServerType.OPERATOR;
    }
}
