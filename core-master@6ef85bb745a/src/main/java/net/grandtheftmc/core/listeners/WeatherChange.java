package net.grandtheftmc.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.grandtheftmc.core.Core;

public class WeatherChange implements Listener {
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (Core.getSettings().stopWeatherChange(e.getWorld().getName())) {
            if (e.toWeatherState()) {
                e.setCancelled(true);
            }
        }
    }

}
