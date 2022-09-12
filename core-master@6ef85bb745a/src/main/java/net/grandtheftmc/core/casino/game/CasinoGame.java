package net.grandtheftmc.core.casino.game;

import net.grandtheftmc.core.casino.Casino;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface CasinoGame {

    /**
     * Identifier of the casino game.
     * @return
     */
    int getIdentifier();

    /**
     * Name of the casino game.
     * @return
     */
    String getName();

    /**
     * Version of the casino game.
     * @return
     */
    String getVersion();

    /**
     * Get the current state of the casino game.
     * @return
     */
    CasinoGameState getState();

    /**
     * Set the current state of the casino game.
     * @param state
     */
    void setState(CasinoGameState state);

    /**
     * This method will run when the Game is added<br>
     * To the list of valid games in Casino.
     */
    void enable();

    /**
     * This method will run when the Game is removed<br>
     * From the list of valid games in Casino.
     */
    void disable();

    /**
     * This is used so the Handler knows,
     * which slot machine was interacted with.
     *
     * @param entity
     * @return
     */
    boolean isClicked(Entity entity);

    /**
     * This will be used when a casino game is won.
     *
     * @param type
     */
    void announce(Player player, int type, int reward);

    boolean isInProgress();

    Casino getCasino();

    boolean registered();

    Location getOriginLocation();
}
