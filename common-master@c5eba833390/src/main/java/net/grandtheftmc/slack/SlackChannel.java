package net.grandtheftmc.slack;

/**
 * Created by Luke Bingham on 29/08/2017.
 */
public enum  SlackChannel {

    DEVELOP("#develop"),
    DEVELOP_ALERTS("#develop_alerts"),
    ENVIRONMENT("#environment"),
    IMPORTANT("#important"),
    PRODUCTION_ALERTS("#production_alerts"),
    SENTRY("#sentry"),
    TRELLO("#trello"),

    ;

    private String channelId;

    SlackChannel(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelId() {
        return channelId;
    }
}
