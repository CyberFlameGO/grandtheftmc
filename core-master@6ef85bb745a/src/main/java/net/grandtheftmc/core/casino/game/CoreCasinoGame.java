package net.grandtheftmc.core.casino.game;

import net.grandtheftmc.core.casino.Casino;
import net.grandtheftmc.core.casino.game.event.CasinoGameStateChangeEvent;
import org.bukkit.Bukkit;

@CasinoGameAttribute(id = 0, name = "Casino Game", version = "1.0")
public abstract class CoreCasinoGame implements CasinoGame {

    protected final Casino casino;
    private CasinoGameState gameState;

    /**
     * Construct a new Casino Game
     */
    public CoreCasinoGame(Casino casino) {
        this.casino = casino;
        this.gameState = CasinoGameState.IDLE;
    }

    @Override
    public int getIdentifier() {
        return this.getClass().getAnnotation(CasinoGameAttribute.class).id();
    }

    @Override
    public String getName() {
        return this.getClass().getAnnotation(CasinoGameAttribute.class).name();
    }

    @Override
    public String getVersion() {
        return this.getClass().getAnnotation(CasinoGameAttribute.class).version();
    }

    @Override
    public CasinoGameState getState() {
        return this.gameState;
    }

    @Override
    public void setState(CasinoGameState state) {
        CasinoGameStateChangeEvent event = new CasinoGameStateChangeEvent(this, this.gameState, state);
        Bukkit.getPluginManager().callEvent(event);

        this.gameState = state;
    }

    @Override
    public Casino getCasino() {
        return this.casino;
    }
}
