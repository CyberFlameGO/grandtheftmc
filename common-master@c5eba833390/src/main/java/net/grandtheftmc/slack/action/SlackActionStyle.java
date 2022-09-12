package net.grandtheftmc.slack.action;

/**
 * Created by Luke Bingham on 05/09/2017.
 */
public enum  SlackActionStyle {
    DEFAULT("default"),
    PRIMARY("primary"),
    DANGER("danger");

    private String code;

    SlackActionStyle(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
