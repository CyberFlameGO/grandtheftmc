package net.grandtheftmc.core.listeners;

/*import com.dsh105.echopet.api.EchoPetAPI;
import com.dsh105.echopet.compat.api.entity.IPet;
import com.dsh105.echopet.compat.api.event.PetInteractEvent;
import net.grandtheftmc.core.Core;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;*/
import org.bukkit.event.Listener;

/**
 * Created by Liam on 11/09/2016.
 */
public class PetListener implements Listener {

    /*@EventHandler(priority = EventPriority.LOW)
    public void onPetInteract(PetInteractEvent e) {
        Player player = e.getPlayer();
        IPet pet = e.getPet();
        PetInteractEvent.Action i = e.getAction();
        if (i == PetInteractEvent.Action.LEFT_CLICK) {
            if (pet.getOwner().equals(player)) {
                pet.ownerRidePet(true);
                e.setCancelled(true);
            } else e.setCancelled(!Core.getSettings().isPetsVulnerable());
        } else if (i == PetInteractEvent.Action.RIGHT_CLICK) {
            if (!pet.getOwner().equals(player)) return;
            e.setCancelled(true);
            if (player.isSneaking()) pet.ownerRidePet(true);
            else EchoPetAPI.getAPI().openPetDataMenu(player, false);
        }
    }*/

}
