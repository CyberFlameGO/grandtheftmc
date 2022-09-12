package net.grandtheftmc.core.casino;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.casino.coins.CoinManager;
import net.grandtheftmc.core.casino.game.CasinoGame;
import net.grandtheftmc.core.casino.game.component.CasinoGameComponent;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.core.util.title.NMSTitle;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class CoreCasino<T extends JavaPlugin> implements Casino {

    private final List<CasinoGame> games;
    private final NMSTitle title;
    private final NMSVersion nmsVersion;
    private final CoinManager coinManager;

    public CoreCasino(T plugin, NMSTitle title, NMSVersion version) {
        this.games = Lists.newArrayList();
        this.coinManager = new CoinManager();
        this.nmsVersion = version;
        Bukkit.getPluginManager().registerEvents(new CasinoGameComponent<T>(this), plugin);
        this.title = title;

        new BukkitRunnable() {
            @Override public void run() {
                refreshIndex();
            }
        }.runTaskTimer(plugin, 20*10, 20*10);
    }

    public CoinManager getCoinManager() {
        return this.coinManager;
    }

    /**
     * Add a casino game to the list of valid games.
     *
     * @param game - Casino Game
     */
    @Override
    public void addGame(CasinoGame game) {
        if (game instanceof Component<?, ?>)
            Bukkit.getPluginManager().registerEvents((Component<?, ?>) game, Core.getInstance());

        this.games.add(game);
    }



    @Override
    public NMSTitle getTitle() {
        return this.title;
    }

    @Override
    public void refreshAll() {
        this.games.forEach(game -> {
            if(!game.isInProgress()) {
                game.disable();
                game.enable();
            }
        });
    }

    private void refreshIndex() {
        for(CasinoGame game : this.games) {
            if(!game.getOriginLocation().getChunk().isLoaded()) game.getOriginLocation().getChunk().load();
            for (Entity entity : game.getOriginLocation().getWorld().getEntities()) {
                if(game.isClicked(entity)) continue;
                if (entity instanceof ArmorStand && entity.getLocation().distance(game.getOriginLocation()) < 0.7) {
                    entity.remove();
                }
            }
        }
    }

    @Override
    public NMSVersion getVersion() {
        return this.nmsVersion;
    }

    @Override
    public void enabledAllGames() {
        for(CasinoGame game : this.games) {
            if(!game.getOriginLocation().getChunk().isLoaded()) game.getOriginLocation().getChunk().load();
            for (Entity entity : game.getOriginLocation().getWorld().getEntities()) {
                if (entity instanceof ArmorStand && entity.getLocation().distance(game.getOriginLocation()) < 20) {
                    entity.remove();
                }
            }
        }

        this.games.forEach(CasinoGame::enable);
    }

    /**
     * Remove casino game from the list of valid games.
     *
     * @param game - Casino Game
     */
    @Override
    public void removeGame(CasinoGame game) {
        if (game instanceof Component<?, ?>)
            HandlerList.unregisterAll((Component<?, ?>) game);

        this.games.remove(game);
        game.disable();
    }

    /**
     * Remove all current casino games.
     */
    @Override
    public void removeAllGames() {
        this.games.forEach(game -> {
            if (game instanceof Component<?, ?>) HandlerList.unregisterAll((Component<?, ?>) game);
            game.disable();
        });

        this.getGames().clear();
    }



    /**
     * Get the currently available Casino Games.
     *
     * @return
     */
    @Override
    public List<CasinoGame> getGames() {
        return this.games;
    }
}
