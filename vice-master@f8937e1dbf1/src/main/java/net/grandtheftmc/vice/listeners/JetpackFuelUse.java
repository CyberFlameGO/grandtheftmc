package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedvehicles.api.events.FuelUseEvent;
import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Timothy Lampen on 8/22/2017.
 */
public class JetpackFuelUse implements Listener {

    @EventHandler
    public void onFuelUse(FuelUseEvent event){
        Player player = event.getPlayer();
        if(player.isOnline() && player.isValid()){
            ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            if(user.getCheatCodeState(CheatCode.NOFUEL).getState()== State.ON) {
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
                boolean badPlayersNearby = Bukkit.getOnlinePlayers().stream().anyMatch(target -> {
                    if(!target.getWorld().equals(player.getWorld()) || target.getLocation().distance(player.getLocation())>100) {
                        return false;
                    }
                    FPlayer fTarget = FPlayers.getInstance().getByPlayer(target);
                    if(fTarget.getRelationTo(fPlayer)==Relation.ENEMY)
                        return true;
                    return false;
                });
                if(badPlayersNearby) {//there are enemies nearby so the fuel is going to be used.
                    return;
                }
                Faction cFac = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
                if (cFac.isWilderness() || cFac.equals(fPlayer.getFaction()) || fPlayer.getFaction().getRelationTo(cFac) != Relation.ENEMY) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
