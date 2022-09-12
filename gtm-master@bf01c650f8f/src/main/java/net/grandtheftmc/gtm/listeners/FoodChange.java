package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.gtm.GTM;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.Objects;

public class FoodChange implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (Objects.equals(event.getEntity().getLocation().getWorld(), GTM.getWarpManager().getSpawn().getLocation().getWorld())) {
            event.setFoodLevel(20);
        }
    }
}
