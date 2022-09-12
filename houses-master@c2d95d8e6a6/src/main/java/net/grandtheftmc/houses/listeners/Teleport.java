package net.grandtheftmc.houses.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.events.TPEvent;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Teleport implements Listener {

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.END_PORTAL) return;
        Player player = e.getPlayer();
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInsideHouse()) {
            user.setInsideHouse(-1);
            user.updateVisibility(player);
        }
        if (user.isInsidePremiumHouse()) user.setInsidePremiumHouse(-1);
    }

    @EventHandler
    public void onTP(TPEvent e) {
        if (e.getTarget() == null)
            return;
        Player player = e.getPlayer();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        switch (e.getType()) {
            case TP_COMPLETE:
                HouseUser targetUser = Houses.getUserManager().getLoadedUser(e.getTarget().getUniqueId());
                if (targetUser.isInsideHouse()) {
                    e.setTargetLocation(
                            Houses.getHousesManager().getHouse(targetUser.getInsideHouse()).getDoor().getOutsideLocation());
                } else if (targetUser.isInsidePremiumHouse()) {
                    e.setTargetLocation(Houses.getHousesManager().getPremiumHouse(targetUser.getInsidePremiumHouse()).getDoor()
                            .getOutsideLocation());
                }
                return;
            case PREMIUM_HOUSE_LEAVE:
            case HOUSE_LEAVE:
                user.setLastTeleport();
                break;
            default:
                break;
        }
    }

}
