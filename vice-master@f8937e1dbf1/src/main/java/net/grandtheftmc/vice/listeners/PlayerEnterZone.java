package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.world.ViceSelection;
import net.grandtheftmc.vice.world.ZoneFlag;
import net.grandtheftmc.vice.world.events.PlayerEnterZoneEvent;
import net.grandtheftmc.vice.world.warps.Warp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Timothy Lampen on 2017-08-28.
 */
public class PlayerEnterZone implements Listener {

    @EventHandler
    public void onZoneEnter(PlayerEnterZoneEvent event) {
        Player player = event.getPlayer();
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        ViceSelection zone = event.getZone();
        for(ZoneFlag flag : zone.getFlags()) {
            switch (flag) {
                case COP_TELEPORT_STATION: {
                    if (user.isCop()) {
                        Warp w = Vice.getWorldManager().getWarpManager().getWarp("police-station");
                        if (w == null) {
                            Core.error("Attempted to TP cop to station, but couldnt as warp police-station hasnt been set.");
                            return;
                        }
                        player.teleport(w.getLocation());
                        player.sendMessage(Lang.COP_MODE.f("&7You have been teleported to the police station because you are not allowed in this area!"));
                    }
                }
            }
        }
    }
}
