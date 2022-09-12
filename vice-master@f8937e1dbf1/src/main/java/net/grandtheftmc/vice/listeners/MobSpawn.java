package net.grandtheftmc.vice.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

/**
 * Created by Timothy Lampen on 2017-08-17.
 */
public class MobSpawn implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        LivingEntity e = event.getEntity();
        if(event.getSpawnReason()!= CreatureSpawnEvent.SpawnReason.CUSTOM && e.getLocation().getWorld().getName().equals("spawn"))
            event.setCancelled(true);
    }

    // HIGHEST to override any other plugins, currently works if not disabled by another plugin.
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onWitherSpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.WITHER) {
            event.setCancelled(true);
        }
    }
}
