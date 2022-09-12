package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.TaxiTarget;
import net.grandtheftmc.gtm.warps.Warp;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;

public class PortalEnter implements Listener {

    @EventHandler
    public void playerPortalEvent(EntityPortalEnterEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (gtmUser.getTaxiTarget() != null) return;
        if (event.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            Warp randomWarp = GTM.getWarpManager().getRandomWarp();
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1F, 1F);
            GTM.getWarpManager().warp(player, user, GTM.getUserManager().getLoadedUser(player.getUniqueId()),
                    new TaxiTarget(randomWarp), 0, user.isPremium() ? 1 : 10);
        }
    }
}
