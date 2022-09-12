package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by Timothy Lampen on 2017-08-07.
 */
public class CommandPreProcess implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        String msg = event.getMessage().toLowerCase();

        if(user.isArrested()){
            if(msg.contains("spawn") || msg.contains("home") || msg.contains("tp") || msg.contains("warp")){
                event.setCancelled(true);
                player.sendMessage(Lang.COP.f("&7You cannot issue this command while you are arrested!"));
                return;
            }
        }

        for(CheatCode code : CheatCode.values()) {
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
