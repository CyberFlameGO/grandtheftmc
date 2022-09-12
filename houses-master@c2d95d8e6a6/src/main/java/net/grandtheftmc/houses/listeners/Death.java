package net.grandtheftmc.houses.listeners;

import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Death implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInsideHouse()) {
            user.setInsideHouse(-1);
            user.updateVisibility(player);
        }
    }
}
