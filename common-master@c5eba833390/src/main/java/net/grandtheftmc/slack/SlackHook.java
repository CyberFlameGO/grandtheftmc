package net.grandtheftmc.slack;

/**
 * Created by Luke Bingham on 29/08/2017.
 */
public enum SlackHook {

    MONTHLY_VOTES("Voters", "https://hooks.slack.com/services/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"),
    SERVER_HEARTBEAT("Heartbeat", "https://hooks.slack.com/services/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"),
    ;

    private final String name, hook;

    SlackHook(String name, String hook) {
        this.name = name;
        this.hook = hook;
    }

    public String getName() {
        return name;
    }

    public String getHook() {
        return hook;
    }
}
