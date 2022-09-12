package net.grandtheftmc.vice.season;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.hologram.HologramManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SeasonManager implements Component <SeasonManager, Vice> {

    static final int SEASON = 2;

    private List<Season> seasons;
    private Season current;

    private final SeasonListener seasonListener;

    public SeasonManager(JavaPlugin plugin, HologramManager hologramManager) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            this.seasons = SeasonDAO.getSeasons(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.current = getCurrentSeason();

        if (this.current != null) {
            Bukkit.getConsoleSender().sendMessage(C.AQUA + "Vice s" + this.current.getNumber());
        }

        this.seasonListener = new SeasonListener(this, plugin, hologramManager);
    }

    @Override
    public SeasonManager onDisable(Vice plugin) {
        if (seasonListener != null) {
            if (seasonListener.task != null)
                this.seasonListener.task.cancel();

            if (seasonListener.hologram != null)
                this.seasonListener.hologram.destroy();
        }

        return this;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public Season getCurrentSeason() {
        return this.current == null ? seasons.stream().filter(Season::isCurrent).findFirst().orElse(null) : this.current;
    }

    public boolean hasEnded() {
        return this.getCurrentSeason().getExpire().getTime() >= System.currentTimeMillis();
    }
}
