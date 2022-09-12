package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.example.Cocaine;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.Optional;

public class Move implements Listener {

    private int spawnWorldHashcode = -1;

    @EventHandler
    public void onMove(PlayerMoveEvent event) {


        Location to = event.getTo(), from = event.getFrom();

        if (spawnWorldHashcode == -1 && event.getPlayer().getWorld().getName().equalsIgnoreCase("spawn")) {
            //set the hashcode
            spawnWorldHashcode = event.getPlayer().getWorld().hashCode();
        }

        if (spawnWorldHashcode == event.getPlayer().getWorld().hashCode()) {
            //No need for checks in the spawn world
            return;
        }

        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            //Player has not moved block
            return;
        }

        Player player = event.getPlayer();

        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());

        if (GTM.getWarpManager().cancelTaxi(player, GTM.getUserManager().getLoadedUser(player.getUniqueId()))) {
            player.sendMessage(Lang.TAXI.f("&eYou moved! Teleportation cancelled!"));
            return;
        }

        if (player.isGliding()) {
            if (houseUser.isInsideHouse() || houseUser.isInsidePremiumHouse()) return;
            if (!player.isSneaking() || player.getLocation().getY() > 200) return;
            double pitch = -event.getTo().getPitch();
            if (pitch < 10 || pitch > 90) return;
            Vector vector = player.getLocation().getDirection();
            player.setVelocity(vector.multiply(1.6));
            player.getWorld().playSound(event.getFrom(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 2.0F);
            return;
        }

        if (!Core.getSettings().isSister()) {
            Optional<Drug> cocaine = ((DrugService) GTM.getDrugManager().getService()).getDrug("cocaine");

            if (cocaine.isPresent()) {
                if (((Cocaine) cocaine.get()).cantMove(player.getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
