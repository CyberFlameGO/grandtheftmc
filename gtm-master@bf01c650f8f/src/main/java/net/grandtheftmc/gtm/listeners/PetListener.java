package net.grandtheftmc.gtm.listeners;

/* com.dsh105.echopet.compat.api.event.PetInteractEvent;
import com.dsh105.echopet.compat.api.event.PetTeleportEvent;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;*/
import org.bukkit.event.Listener;

public class PetListener implements Listener {

    /*@EventHandler
    public void onTeleport(PetTeleportEvent e) {
        Player player = e.getPet().getOwner();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInCombat() || user.isArrested())
            e.setCancelled(true);
    }

    @EventHandler
    public void onPetInteract(PetInteractEvent e) {
        Player player = e.getPet().getOwner();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInCombat() || user.isArrested())
            e.setCancelled(true);
    }*/

}
