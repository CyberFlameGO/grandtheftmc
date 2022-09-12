package net.grandtheftmc.core.alert.type;

/**
 * Created by Luke Bingham on 10/09/2017.
 */
public enum AlertType {
    NEWS(true),
    SALE(false),

    POLL(false),
    ;

    private final boolean enabled;

    AlertType(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public AlertType next() {
        if(this == NEWS) return SALE;
        if(this == SALE) return NEWS;
        return NEWS;
    }
}
