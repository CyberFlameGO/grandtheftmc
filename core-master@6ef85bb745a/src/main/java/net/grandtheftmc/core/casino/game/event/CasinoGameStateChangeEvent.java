package net.grandtheftmc.core.casino.game.event;

import net.grandtheftmc.core.casino.game.CasinoGame;
import net.grandtheftmc.core.casino.game.CasinoGameState;
import net.grandtheftmc.core.events.CoreEvent;

public class CasinoGameStateChangeEvent extends CoreEvent {

    private final CasinoGame casinoGame;
    private final CasinoGameState from, to;

    /**
     * Construct a new Event
     */
    public CasinoGameStateChangeEvent(CasinoGame casinoGame, CasinoGameState from, CasinoGameState to) {
        super(false);
        this.casinoGame = casinoGame;
        this.from = from;
        this.to = to;
    }

    /**
     * Get the casino game.
     * @return
     */
    public CasinoGame getCasinoGame() {
        return casinoGame;
    }

    /**
     * Get the previous casino game state.
     * @return
     */
    public CasinoGameState getFrom() {
        return from;
    }

    /**
     * Get the new casino game state.
     * @return
     */
    public CasinoGameState getTo() {
        return to;
    }
}
