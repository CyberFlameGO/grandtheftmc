package net.grandtheftmc.houses.listeners;

import com.j0ach1mmall3.wastedguns.api.events.ranged.RangedWeaponShootEvent;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WeaponShoot implements Listener {

    @EventHandler
    public void rangedWeaponShoot(RangedWeaponShootEvent event) {
        if (event.getLivingEntity().getType() != EntityType.PLAYER) return;
        Player player = (Player) event.getLivingEntity();
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (houseUser.isTeleporting()) {
            event.setCancelled(true);
        }
    }
}
