package net.grandtheftmc.vice.season;

import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.hologram.Hologram;
import net.grandtheftmc.vice.hologram.HologramManager;
import net.grandtheftmc.vice.hologram.TypeWriter;
import net.grandtheftmc.vice.hologram.event.HologramReceiveEvent;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateException;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateNodeException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SeasonListener implements Listener {

    private final SeasonManager seasonManager;
    protected Hologram hologram;
    BukkitTask task;

    private final TypeWriter typeWriter;
    private int progress = 1;

    SeasonListener(SeasonManager seasonManager, JavaPlugin plugin, HologramManager hologramManager) {
        this.seasonManager = seasonManager;
        this.typeWriter = new TypeWriter("&d&lVice&f&lMC Season &d&l2", 15);

        ServerUtil.runTaskLater(() -> {
            Location origin = new Location(Bukkit.getWorld("spawn"), 137.5, 82, 235.5);
            try {
                this.hologram = hologramManager.create(2, origin);
                this.hologram.addNode(1);
                this.hologram.addNode(2);
                this.hologram.addNode(3);
                this.hologram.addNode(4);

                this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    this.hologram.refresh(1);

                    if (progress % 10 == 0) {
                        this.hologram.refresh(4);
                        this.progress = 0;
                    }

                    progress++;
                }, 20L, 3L);
            }
            catch (HologramDuplicateException | HologramDuplicateNodeException e) {
                e.printStackTrace();
            }
        }, 20*5);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    protected final void onPlayerJoin(PlayerJoinEvent event) {
        if (this.hologram == null) return;
        ServerUtil.runTaskLaterAsync(() -> this.hologram.spawn(event.getPlayer()), 20L);
    }

    @EventHandler
    protected final void onHologramReceive(HologramReceiveEvent event) {
        int id = event.getHologram().getId();
        int nodeId = event.getNode().getId();

        if(id != 2) return;

        if (nodeId == 1) {
            event.setText(this.typeWriter.next());
        }

        else if (nodeId == 2) {
            event.setText(" ");
            event.setDisplay(false);
        }

        else if (nodeId == 3) {
            event.setText("&c&lSeason ends in");
        }

        else if (nodeId == 4) {
            long difference = (seasonManager.getCurrentSeason().getExpire().getTime() - System.currentTimeMillis()) / 1000;
            event.setText(Utils.timeInSecondsToText(difference, C.DARK_RED + C.BOLD, C.RED, C.WHITE));
        }
    }
}
