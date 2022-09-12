package net.grandtheftmc.vice.display;

import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.display.cont.CartelListener;
import net.grandtheftmc.vice.display.cont.PlayerStatsListener;
import net.grandtheftmc.vice.hologram.HologramManager;

public class DisplayManager implements Component <DisplayManager, Vice> {

    // TODO: Find a better place for this to go

    private final PlayerStatsListener playerStatsListener;
    private final CartelListener cartelListener;

    public DisplayManager(Vice plugin, HologramManager hologramManager) {
        this.playerStatsListener = new PlayerStatsListener(plugin, hologramManager);
        this.cartelListener = new CartelListener(plugin, hologramManager);
    }

    @Override
    public DisplayManager onDisable(Vice plugin) {
        this.playerStatsListener.task.cancel();
        this.cartelListener.task[0].cancel();
        this.cartelListener.task[1].cancel();

        this.playerStatsListener.hologram.destroy();
        this.cartelListener.hologram[0].destroy();
        this.cartelListener.hologram[1].destroy();

        return this;
    }

}
