package net.grandtheftmc.core.casino.game.event;

import net.grandtheftmc.core.casino.game.CasinoGame;
import net.grandtheftmc.core.events.CoreEvent;
import org.bukkit.entity.Player;

public class CasinoGameEndEvent extends CoreEvent {

    private final CasinoGame game;
    private final Player player;

    /**
     * Construct a new Event
     */
    public CasinoGameEndEvent(CasinoGame game, Player player) {
        super(false);
        this.game = game;
        this.player = player;
    }

    public CasinoGame getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }
}
