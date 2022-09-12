package net.grandtheftmc.vice.areas.tasks;

import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.areas.AreaManager;
import net.grandtheftmc.vice.areas.events.AreaEnterEvent;
import net.grandtheftmc.vice.areas.events.AreaLeaveEvent;
import net.grandtheftmc.vice.areas.obj.Area;
import org.bukkit.Bukkit;

import java.util.UUID;

public class AreaUpdater implements Runnable {

    private final AreaManager areaManager;

    public AreaUpdater(AreaManager areaManager) {
        this.areaManager = areaManager;
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            UUID uuid = player.getUniqueId();
            String world = player.getLocation().getWorld().getName();
            int x = player.getLocation().getBlockX();
            int z = player.getLocation().getBlockZ();

            ServerUtil.runTaskAsync(() -> {
                for(Area area : areaManager.getAreas()) {
                    if (!area.isInside(x, z, world, true)) {

                        if (areaManager.isAt(uuid, area))
                            ServerUtil.runTask(() -> Bukkit.getPluginManager().callEvent(new AreaLeaveEvent(player, area)));

                        continue;
                    }

                    if (!areaManager.isAt(uuid, area))
                        ServerUtil.runTask(() -> Bukkit.getPluginManager().callEvent(new AreaEnterEvent(player, area)));
                }
            });
        });
    }

}
