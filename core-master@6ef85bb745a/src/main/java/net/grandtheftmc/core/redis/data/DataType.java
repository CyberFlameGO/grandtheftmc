package net.grandtheftmc.core.redis.data;

public enum DataType {

    //Declares when a player has received some rewards (if live on the server to notify them)
    //Can contain multiple reward notifications, checks are done on MySQL
    REWARD_NOTIFY("rewards"),
	VOTE_NOTIFY("vote_notify");

    private final String identifier;

    DataType(String identifier) {
        this.identifier = identifier;
    }
}
