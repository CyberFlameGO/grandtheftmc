package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Timothy Lampen on 2017-09-25.
 */
public class CommandPreProcess implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        String msg = event.getMessage().toLowerCase();

        if(player.getGameMode()== GameMode.SPECTATOR && !Core.getUserManager().getLoadedUser(player.getUniqueId()).isStaff()) {
            player.sendMessage(Lang.GTM.f("&7You cannot execute commands while in spectator mode!"));
            event.setCancelled(true);
            return;
        }


        for(CheatCode code : CheatCode.getCodes()) {
            switch (code){
                case FEED:
                case STACK:
                    continue;
            }
            if(msg.equalsIgnoreCase("/" + code.toString())) {
                code.activate(Core.getUserManager().getLoadedUser(player.getUniqueId()), user, player, user.getCheatCodeState(code));
                event.setCancelled(true);
            }
        }
    }
}