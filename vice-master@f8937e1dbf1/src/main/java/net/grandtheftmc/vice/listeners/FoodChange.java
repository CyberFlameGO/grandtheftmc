package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.vice.Vice;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.Objects;

public class FoodChange implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (Objects.equals(event.getEntity().getLocation().getWorld(), Vice.getWorldManager().getWarpManager().getSpawn().getLocation().getWorld())) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    protected final void onItemConsume(PlayerItemConsumeEvent event) {
        if(event.getItem() == null) return;
        if(event.getItem().getType() != Material.CHORUS_FRUIT) return;

        event.setCancelled(true);
    }
}
