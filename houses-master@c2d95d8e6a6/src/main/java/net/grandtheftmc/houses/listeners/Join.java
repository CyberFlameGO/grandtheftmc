package net.grandtheftmc.houses.listeners;

import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        user.updateVisibility(player);

        user.loadChests((call) -> {});
    }
}
