package net.grandtheftmc.vice.users;


import net.grandtheftmc.core.util.State;
/**
 * Created by Timothy Lampen on 2017-08-26.
 */
public class CheatCodeState {

    private final State state;
    private final boolean purchased;

    public CheatCodeState(State state, boolean purchased) {
        this.state = state;
        this.purchased = purchased;
    }

    public State getState() {
        return state;
    }

    public boolean isPurchased() {
        return purchased;
    }
}
