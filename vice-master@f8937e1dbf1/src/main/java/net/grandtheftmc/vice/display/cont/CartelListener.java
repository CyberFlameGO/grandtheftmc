package net.grandtheftmc.vice.display.cont;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.hologram.CoreHologram;
import net.grandtheftmc.vice.hologram.Hologram;
import net.grandtheftmc.vice.hologram.HologramManager;
import net.grandtheftmc.vice.hologram.event.HologramReceiveEvent;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateException;
import net.grandtheftmc.vice.hologram.exception.HologramDuplicateNodeException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.text.NumberFormat;
import java.util.*;

public final class CartelListener implements Listener {

    private ArrayList<Faction> factionList = Lists.newArrayList();

    public final Hologram[] hologram;
    public final BukkitTask[] task;

    public CartelListener(JavaPlugin plugin, HologramManager hologramManager) {
        this.hologram = new Hologram[2];
        this.task = new BukkitTask[2];

        ServerUtil.runTaskLater(() -> {
            Location origin = new Location(Bukkit.getWorld("spawn"), 145.5, 82, 220.5); // TODO: Figure out where this needs to go

            try {

                this.hologram[0] = hologramManager.create(4, origin);
                this.hologram[0].addNode(1);
                this.hologram[0].addNode(2);
                this.hologram[0].addNode(3);
                this.hologram[0].addNode(4);

                this.hologram[1] = hologramManager.create(5, new Location(Bukkit.getWorld("spawn"), 121.5, 80, 235.5));
                for (int i = 1; i < 15; i++) hologram[1].addNode(i);

                task[1] = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    ServerUtil.runTask(() -> {
                        factionList.clear();

                        factionList = Factions.getInstance().getAllFactions();
                        factionList.remove(Factions.getInstance().getWilderness());
                        factionList.remove(Factions.getInstance().getSafeZone());
                        factionList.remove(Factions.getInstance().getWarZone());
                        factionList.sort((o1, o2) -> {
//                            double f1Worth = Math.pow(o1.getLandRounded() * 64, 2) + o1.getStash();
//                            double f2Worth = Math.pow(o2.getLandRounded() * 64, 2) + o2.getStash();

                            double f1Worth = o1.getStash();
                            if (o1.getAllClaims().size() > 0) f1Worth += 50000 * 1.05 * o1.getAllClaims().size();

                            double f2Worth = o2.getStash();
                            if (o2.getAllClaims().size() > 0) f2Worth += 50000 * 1.05 * o2.getAllClaims().size();

                            if (f1Worth < f2Worth)
                                return 1;
                            else if (f1Worth > f2Worth)
                                return -1;
                            return 0;
                        });
                    });
                }, 10L, 5 * 60 * 20);

                // Update Cartel stats every 15 seconds
                task[0] = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                    this.hologram[0].refresh();
                    this.hologram[1].refresh();
                }, 20L, 15 * 20L);

            } catch (HologramDuplicateException | HologramDuplicateNodeException e) {
                e.printStackTrace();
            }
        }, 5 * 20L);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onPlayerJoin(PlayerJoinEvent event) {
        if (this.hologram[0] != null) {
            ServerUtil.runTaskLaterAsync(() -> this.hologram[0].spawn(event.getPlayer()), 20L);
        }

        if (this.hologram[1] != null) {
            ServerUtil.runTaskLaterAsync(() -> this.hologram[1].spawn(event.getPlayer()), 20L);
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onHologramReceive(HologramReceiveEvent event) {
        int id = event.getHologram().getId();
        int nodeId = event.getNode().getId();

        if (id == 4) {
            Player player = event.getPlayer();
            Faction cartel = FPlayers.getInstance().getByPlayer(player).getFaction();

            // The player is not in a Cartel so lets display a little info message instead
            if (cartel == null || cartel.isWilderness() || cartel.isWarZone()) {
                if (nodeId == 1) {
                    event.setText("&6&lCartels");
                } else if (nodeId == 2) {
                    event.setText("&eCreate or join a Cartel and conquer Vice!");
                } else if (nodeId == 3) {
                    event.setText(" ");
                    event.setDisplay(false);
                } else if (nodeId == 4) {
                    event.setText(" ");
                    event.setDisplay(false);
                }

                return;
            }

            int online = cartel.getOnlinePlayers().size();
            int size = cartel.getSize();
            int kills = cartel.getKills();
            int deaths = cartel.getDeaths();
            double stash = cartel.getStash();
            String formattedStash = NumberFormat.getCurrencyInstance(Locale.US).format(stash);

            // Cartel name
            if (nodeId == 1) {
                event.setText("&6&l" + ChatColor.stripColor(cartel.getTag()));
            }

            // Cartel member count
            else if (nodeId == 2) {
                event.setText("&e" + online + "/" + size + " members online");
            }

            // Cartel balance or 'stash'
            else if (nodeId == 3) {
                event.setText("&eStash&f: " + formattedStash); // NumberFormat adds the '$'
            }

            // Cartel Kills and Deaths
            else if (nodeId == 4) {
                event.setText("&aKills&f: " + kills + " &e| " + "&cDeaths&f: " + deaths);
            }
        }

        else if (id == 5) {
            if (nodeId == 1) {
                event.setText(C.GOLD + C.BOLD + "TOP CARTELS");
                return;
            }

            if (nodeId == 12) {
                event.setDisplay(false);
                return;
            }

            if (nodeId == 13) {
                event.setText(C.YELLOW + C.ITALIC + "The Cartel leaderboard is");
                return;
            }

            if (nodeId == 14) {
                event.setText(C.YELLOW + C.ITALIC + "calculated via 'stash * land worth'");
                return;
            }

            int correctNode = nodeId - 2;
            if (this.factionList.size() > correctNode) {
                Faction faction = this.factionList.get(correctNode);

                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                double worth = faction.getStash();
                if (faction.getAllClaims().size() > 0) worth += 50000 * 1.05 * faction.getAllClaims().size();
                event.setText(C.YELLOW + "#" + (correctNode + 1) + C.WHITE + "  " + faction.getTag() + C.GRAY + " - " + C.GREEN + format.format(worth));
                return;
            }

            event.setText(C.YELLOW + "#" + (correctNode + 1) + C.WHITE + "  Unknown" + C.GRAY + " - " + C.GREEN + "$0.00");
        }
    }

}
