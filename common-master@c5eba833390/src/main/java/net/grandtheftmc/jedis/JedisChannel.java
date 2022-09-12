package net.grandtheftmc.jedis;

/**
 * Created by Luke Bingham on 19/08/2017.
 */
public enum JedisChannel {
    GLOBAL("global_network"),
    SERVER_QUEUE("server_queue"),
    DEV("dev"),
    WATCHDAWG("watchdawg"),
    ;

    private String channel;

    JedisChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
