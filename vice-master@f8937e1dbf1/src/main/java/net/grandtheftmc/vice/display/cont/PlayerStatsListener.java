package net.grandtheftmc.vice.display.cont;

import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.hologram.Hologram;
import net.grandtheftmc.vice.hologram.HologramManager;
import net.grandtheftmc.vice.hologram.event.HologramReceiveEvent;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateException;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateNodeException;
import net.grandtheftmc.vice.users.ViceRank;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PlayerStatsListener implements Listener {

    public Hologram hologram;
    public BukkitTask task;

    /**
     * Registers listeners and creates the hologram object
     * @param plugin
     * @param hologramManager
     */
    public PlayerStatsListener(JavaPlugin plugin, HologramManager hologramManager) {
        ServerUtil.runTaskLater(() -> {
            Location origin = new Location(Bukkit.getWorld("spawn"), 127.5, 82, 220.5); // TODO: Figure out where this needs to go

            try {

                this.hologram = hologramManager.create(3, origin);
                this.hologram.addNode(1);
                this.hologram.addNode(2);
                this.hologram.addNode(3);
                this.hologram.addNode(4);
                this.hologram.addNode(5);

                // Updates the stats every 5 seconds
                task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    this.hologram.refresh(2);
                    this.hologram.refresh(3);
                    this.hologram.refresh(4);
                    this.hologram.refresh(5);
                }, 20L, 5 * 20L);

            } catch (HologramDuplicateException | HologramDuplicateNodeException e) {
                e.printStackTrace();
            }
        }, 5 * 20L);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Sends the hologram to the player 1 second after they join
     * @param event
     */
    @EventHandler
    protected final void onPlayerJoin(PlayerJoinEvent event) {
        if (this.hologram == null) return;
        ServerUtil.runTaskLaterAsync(() -> this.hologram.spawn(event.getPlayer()), 20L);
    }

    /**
     * Fires when the player has received their hologram, information is set/removed/updated here
     * @param event
     */
    @EventHandler
    protected final void onHologramReceive(HologramReceiveEvent event) {
        int id = event.getHologram().getId();
        int nodeId = event.getNode().getId();

        if(id != 3) return;

        Player player = event.getPlayer();
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());

        ViceRank rank = viceUser.getRank();
        int kills = viceUser.getKills(), deaths = viceUser.getDeaths(), killstreak = viceUser.getKillStreak();
        double kd = 0.00;
        double money = viceUser.getMoney();
        String formattedMoney = NumberFormat.getCurrencyInstance(Locale.US).format(money);

        if(deaths == 0) // Need to check if deaths is 0, throws an ArithmeticException otherwise
            kd = kills;
        else if(kills > 0)
            kd = kills / deaths;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        // Stats title
        if(nodeId == 1) {
            event.setText("&6&l" + player.getName() + "'s Stats");
        }

        // ViceRank
        else if(nodeId == 2) {
            event.setText("&eRank&f: " + rank.getColoredName());
        }

        // Kills, Deaths
        else if(nodeId == 3) {
            event.setText("&aKills&f: " + kills + " &e| " + "&cDeaths&f: " + deaths);
        }

        // K/D and Killstreaks
        else if(nodeId == 4) {
            event.setText("&aK/D Ratio&f: " + decimalFormat.format(kd) + " &e| " + "&cKillstreak&f: " + killstreak);
        }

        // Money
        else if(nodeId == 5) {
            event.setText("&2Money&f: " + formattedMoney);
        }
    }

}
