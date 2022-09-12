package net.grandtheftmc.slack.action;

/**
 * Created by Luke Bingham on 05/09/2017.
 */
public enum SlackActionType {
    BUTTON("button"),
    SELECT("select");

    private String code;

    SlackActionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
