package net.grandtheftmc.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import net.grandtheftmc.core.Core;

public class HungerChange implements Listener {

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent e) {
        if (Core.getSettings().stopHungerChange(e.getEntity().getWorld().getName())) {
            e.setFoodLevel(20);
        }
    }
}
