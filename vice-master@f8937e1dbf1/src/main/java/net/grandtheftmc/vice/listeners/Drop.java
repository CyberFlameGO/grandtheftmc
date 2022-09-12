package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.GameItem;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class Drop implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack().clone();
        switch (item.getType()) {
            case CHEST:
            case WATCH:
            case COMPASS:
                if(ViceUtils.isDefaultPlayerItem(item)) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            e.getItemDrop().remove();
                        }
                    }.runTaskLater(Vice.getInstance(), 1);
                    ViceUtils.giveGameItems(e.getPlayer());
                }
                break;
            default:
                if (Objects.equals(e.getPlayer().getWorld().getName(), "spawn")) {
                    GameItem gi = Vice.getItemManager().getItem(item);
                    if (gi != null && gi.isScheduled()) {
                        e.setCancelled(true);
                        e.getPlayer().sendMessage(Lang.DRUGS.f("&7You can't drop drugs here! Go sell them at the nearest drug dealer."));
                    }
                }
                break;
        }
    }

    @EventHandler
    protected final void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() == null) return;
        if (!(event instanceof Item)) return;

        Item item = (Item) event.getEntity();
        switch (item.getItemStack().getType()) {
            case CHEST:
            case WATCH:
            case COMPASS:
                if (ViceUtils.isDefaultPlayerItem(item.getItemStack())) {
                    event.setCancelled(true);
                }
                break;
        }
    }
}
