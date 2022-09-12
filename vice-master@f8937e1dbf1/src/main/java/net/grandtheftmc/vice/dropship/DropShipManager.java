package net.grandtheftmc.vice.dropship;

import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.AreaManager;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.items.ItemManager;
import net.grandtheftmc.vice.lootcrates.LootCrate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DropShipManager {

    /**
     * Drop item redeem - trigger for nearest city.
     * Alert players in chat 60 seconds before, clickable : teleport
     * Alert players in chat onStart, clickable : teleport
     * Restock chests in city
     *
     * If a player started the dropship, they should be celebrated in chat.
     *
     * Chest drops are the same as they are on GTM - will most likely be slightly balanced / changed going forwards.
     *
     *
     */

    protected final ConcurrentHashMap<UUID, Long> userDamageMap = new ConcurrentHashMap<>();

    private final JavaPlugin plugin;
    private final AreaManager areaManager;

    private DropShip serverDropShip = null;
    private DropShip dropShip = null;

    public DropShipManager(JavaPlugin plugin, AreaManager areaManager, ItemManager itemManager) {
        this.plugin = plugin;
        this.areaManager = areaManager;

        new DropShipHandler<JavaPlugin>(this, itemManager, plugin);
        new DropShipTeleportCommand(this);

        new BukkitRunnable() {
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                for (Area area : areaManager.getAreas()) {
                    if (area.getAreaType() == Area.AreaType.TOWN) continue;

                    for (Chest chest : area.getChests()) {
                        ServerUtil.runTask(() -> {
                            LootCrate lootCrate = Vice.getCrateManager().getCrate(chest.getLocation());
                            if (lootCrate == null) return;
                            lootCrate.restock(Area.DropType.DEFAULT);
                        });
                    }
                }

                Bukkit.broadcastMessage(C.YELLOW + C.BOLD + "DROP SHIP");
                Bukkit.broadcastMessage(C.GOLD + "All settlement chests have been restocked.");
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 60, 20 * 60 * 60 * 8);

        new BukkitRunnable() {
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                for (UUID uuid : userDamageMap.keySet()) {
                    long time = userDamageMap.get(uuid);
                    if (time <= System.currentTimeMillis()) {
                        userDamageMap.remove(uuid);
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20 * 10, 20 * 10);
    }

    /**
     * Start a drop ship event.
     *  @param player - Caller of the drop ship
     * @param area - Settlement to start the event at
     */
    public void startDropShop(boolean major, Player player, Area area) {
        if (this.dropShip != null) return;
        this.dropShip = new DropShip(plugin, this, player, area, major);
        this.dropShip.setDropState(DropShipState.STARTING);
        this.dropShip.start();
    }

    /**
     * Force stop an event.
     */
    public void forceStop() {
        if (this.dropShip == null) return;
        this.dropShip.stop();
    }

    public void reset() {
        this.dropShip = null;
    }

    /**
     * Get the currently active drop ship event.
     *
     * @return currently active drop ship
     */
    public DropShip getDropShip() {
        return dropShip;
    }

    /**
     * Check if a drop ship can be started.
     *
     * @return active state
     */
    public boolean canStartDrop() {
        return this.dropShip == null;
    }

    /**
     * Check if a drop ship event is currently active.
     *
     * @return active state
     */
    public boolean isActive() {
        return !this.canStartDrop();
    }

    /**
     * Get the closest Area to a players current location.
     *
     * @param player - The Player
     * @param callback - The return function of the closest Area
     */
    public void getClosestArea(boolean major, Player player, Callback<Area> callback) {
        ServerUtil.runTaskAsync(() -> {
            Area closest = null;
            double distance = -1;
            for (Area area : this.areaManager.getAreas()) {
                if (area.getAreaType() != Area.AreaType.TOWN) continue;

                double a = getDistanceSquared(player, area.getMaxX(), area.getMaxZ());
                double b = getDistanceSquared(player, area.getMinX(), area.getMinZ());

                if (closest == null) {
                    closest = area;
                    distance = a < b ? a : b;
                    continue;
                }

                if (a < distance) {
                    closest = area;
                    distance = a;
                }

                if (b < distance) {
                    closest = area;
                    distance = b;
                }
            }

            callback.call(closest);
        });
    }

    private double getDistanceSquared(Player player, int x, int z) {
        Location origin = player.getLocation().clone();
        origin.setY(0);
        return origin.distanceSquared(new Location(origin.getWorld(), x, origin.getY(), z));
    }
}
