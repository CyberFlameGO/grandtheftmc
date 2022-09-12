package net.grandtheftmc.houses.listeners;

/*import com.dsh105.echopet.compat.api.event.PetInteractEvent;
import com.dsh105.echopet.compat.api.event.PetTeleportEvent;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseDoor;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;*/
import org.bukkit.event.Listener;

public class PetListener implements Listener {

   /* @EventHandler
    public void onTeleport(PetTeleportEvent e) {
        Player player = e.getPet().getOwner();
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (!houseUser.isInsideHouse()) return;
        e.setCancelled(true);
        House house = Houses.getHousesManager().getHouse(houseUser.getInsideHouse());
        if (house != null) {
            HouseDoor door = house.getDoor();
            if (door != null)
                e.setTo(door.getOutsideLocation());
        }
    }

    @EventHandler
    public void onPetInteract(PetInteractEvent e) {
        Player player = e.getPet().getOwner();
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (houseUser.isInsideHouse() || houseUser.isInsidePremiumHouse()) e.setCancelled(true);
    }*/
}
