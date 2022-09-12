package net.grandtheftmc.core.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;

public class Damage implements Listener {

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
    	
    	// ignore non player entities
    	if (!(event.getEntity() instanceof Player)){
    		return;
    	}
    	
        Player player = (Player) event.getEntity();
        
        User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        if (user != null){
        	if (user.isInTutorial()){
        		event.setCancelled(true);
        	}
        }
    }
}
