package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.gtm.items.ArmorUpgrade;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

/**
 * Created by Timothy Lampen on 7/20/2017.
 */
public class PotionSplash implements Listener {

    @EventHandler
    public void onSplashPotion(PotionSplashEvent event){
        for(Entity e  : event.getAffectedEntities()){
            if(e instanceof Player){
                Player victim = (Player)e;
                if(ArmorUpgrade.playerHasArmorUpgrade(victim, ArmorUpgrade.LEAD_LINED)){
                    event.setIntensity(victim, 0);
                }
            }
        }
    }
}
