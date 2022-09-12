package net.grandtheftmc.gtm.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EnderDragonChangePhaseEvent;

import net.grandtheftmc.gtm.GTM;

/**
 * Created by Timothy Lampen on 2017-08-17.
 */
public class MobSpawn implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event){
        LivingEntity e = event.getEntity();
        
        if (e.getType() == EntityType.ENDER_DRAGON){
        	event.setCancelled(true);
        }

        if (e.hasMetadata("GTM")) return;

        if(e.getType()==EntityType.RABBIT && !GTM.getHolidayManager().getEaster().isActive())
            event.setCancelled(true);
        else if(event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM && event.getEntity().getType() != EntityType.ARMOR_STAND)
            event.setCancelled(true);
    }
    
    @EventHandler
    public void onEnderDragonChangePhase(EnderDragonChangePhaseEvent event){
    	// attempts to stop the enderdragon from changing phases
    	event.setCancelled(true);
    }
}
