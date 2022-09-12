package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.util.State;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.world.ViceSelection;
import net.grandtheftmc.vice.world.ZoneFlag;
import net.grandtheftmc.vice.world.events.PlayerLeaveZoneEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Timothy Lampen on 2017-08-28.
 */
public class PlayerLeaveZone implements Listener{

    @EventHandler
    public void onLeaveZone(PlayerLeaveZoneEvent event) {
        Player player = event.getPlayer();
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        ViceSelection zone = event.getZone();
        for(ZoneFlag flag : zone.getFlags()) {
            switch (flag) {
                case COP_CANT_ARREST: {//player is leaving the safezone
                    if(user.getCheatCodeState(CheatCode.SNEAKY).getState()== State.ON) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*5, 0));
                    }
                }
            }
        }
    }
}
