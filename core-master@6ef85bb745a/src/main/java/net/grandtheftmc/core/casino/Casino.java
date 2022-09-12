package net.grandtheftmc.core.casino;

import net.grandtheftmc.core.casino.game.CasinoGame;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.core.util.title.NMSTitle;

import java.util.List;

public interface Casino {

    /**
     * Get the currently available Casino Games.
     *
     * @return
     */
    List<CasinoGame> getGames();

    /**
     * Remove casino game from the list of valid games.
     *
     * @param game - Casino Game
     */
    void removeGame(CasinoGame game);

    /**
     * Remove all current casino games.
     */
    void removeAllGames();

    /**
     * Add a casino game to the list of valid games.
     *
     * @param game - Casino Game
     */
    void addGame(CasinoGame game);

    NMSTitle getTitle();

    void refreshAll();

    NMSVersion getVersion();

    void enabledAllGames();
}
