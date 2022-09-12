package net.grandtheftmc.houses.listeners;

import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseChest;
import net.grandtheftmc.houses.users.UserHouse;
import net.grandtheftmc.houses.users.UserHouseChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;

public class Leave implements Listener {

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isTeleporting()) {
            user.setTeleporting(false);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        if (user.isInsideHouse() || user.isInsidePremiumHouse()) {
            player.teleport(GTM.getWarpManager().getSpawn().getLocation());
        }

        for(UserHouse userHouse : user.getHouses()) {
            for(UserHouseChest chest : userHouse.getChests()) {
                chest.updateContents(player.getUniqueId(), userHouse.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeaveMonitor(PlayerQuitEvent e) {
        Houses.getUserManager().unloadUser(e.getPlayer().getUniqueId());
    }

}
