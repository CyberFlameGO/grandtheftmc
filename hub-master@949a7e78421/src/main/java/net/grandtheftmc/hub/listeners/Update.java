package net.grandtheftmc.hub.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.hub.HubUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Update implements Listener {

    @EventHandler
    public void onUpdate(UpdateEvent e) {
        Player player = e.getPlayer();
        switch (e.getReason()) {
            case BOARD:
            case BUCKS:
            case TOKENS:
            case RANK:
                HubUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
                break;
            case PREF:
                switch (e.getPref()) {
                    case PLAYERS_SHOWN:
                        User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                        u.setLastPlayersToggle(System.currentTimeMillis());
                        u.updateVisibility(player);
                        break;
                    case USE_SCOREBOARD:
                        HubUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
}
