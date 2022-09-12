package net.grandtheftmc.houses.listeners;

import com.j0ach1mmall3.wastedguns.api.events.explosives.ExplosionDamageEntityEvent;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.Collection;

public class Damage implements Listener {

    @EventHandler
    public void onExplosionDamage(ExplosionDamageEntityEvent event) {
        Collection<LivingEntity> remove = new ArrayList<>();
        event.getVictims().forEach(livingEntity -> {
            if(livingEntity.getType() != EntityType.PLAYER) return;
            Player target = (Player)livingEntity;
            HouseUser houseUser = Houses.getUserManager().getLoadedUser(target.getUniqueId());
            if(houseUser.isInsidePremiumHouse() || houseUser.isInsideHouse()) {
                remove.add(livingEntity);
            }
        });
        event.getVictims().removeAll(remove);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInsideHouse() || user.isInsidePremiumHouse()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) return;
        if (event.getDamager().getType() != EntityType.PLAYER) return;

        HouseUser victim = Houses.getUserManager().getLoadedUser(event.getEntity().getUniqueId());
        HouseUser attacker = Houses.getUserManager().getLoadedUser(event.getEntity().getUniqueId());

        if ((victim.isInsideHouse() || victim.isInsidePremiumHouse())
                || (attacker.isInsideHouse() || attacker.isInsidePremiumHouse())) {
            event.setCancelled(true);
        }
    }
}