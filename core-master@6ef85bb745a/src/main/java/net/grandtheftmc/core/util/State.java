package net.grandtheftmc.core.util;

/**
 * Created by Timothy Lampen on 8/21/2017.
 */
public enum State {
    ON(1),
    OFF(0),
    LOCKED(-1);

    private final int num;
    State(int num){
        this.num = num;
    }

    public int toInt(){
        return num;
    }

    public int getInt() {
        return num;
    }
}
