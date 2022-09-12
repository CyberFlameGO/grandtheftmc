package net.grandtheftmc.core.alert.type;

/**
 * Created by Luke Bingham on 10/09/2017.
 */
public enum AlertShowType {
    ONCE,
    REPEAT,
    ;

    public AlertShowType next() {
        if(this == ONCE) return REPEAT;
        if(this == REPEAT) return ONCE;
        return ONCE;
    }
}
