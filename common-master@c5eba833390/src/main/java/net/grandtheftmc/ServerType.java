package net.grandtheftmc;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public enum ServerType {
    HUB("hub", true),
    GTM("gtm", true),
    VICE("vice", true),
    CREATIVE("creative", true),

    PROXY("proxy", false),
    OPERATOR("operator", false),

    GLOBAL("global", false),
    ;

    private final String serverName;
    private final boolean playable;

    ServerType(String serverName, boolean playable) {
        this.serverName = serverName;
        this.playable = playable;
    }

    public String getServerName() {
        return serverName;
    }

    public boolean isPlayable() {
        return playable;
    }
}
