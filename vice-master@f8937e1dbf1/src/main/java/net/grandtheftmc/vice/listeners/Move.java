package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.drugs.example.Cocaine;
import net.grandtheftmc.vice.world.ViceSelection;
import net.grandtheftmc.vice.world.events.PlayerEnterZoneEvent;
import net.grandtheftmc.vice.world.events.PlayerLeaveZoneEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Optional;

public class Move implements Listener {


    @EventHandler
    public void onMove(PlayerMoveEvent event) {


        Location to = event.getTo(), from = event.getFrom();

        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            //Player has not moved block
            return;
        }

        Player player = event.getPlayer();

        Optional<Drug> cocaine = ((DrugService) Vice.getDrugManager().getService()).getDrug("cocaine");
        if (cocaine.isPresent()) {
            if (((Cocaine) cocaine.get()).cantMove(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
        }


        List<ViceSelection> pastZones = Vice.getWorldManager().getZones(from);

        List<ViceSelection> currentZones = Vice.getWorldManager().getZones(to);
        if(!pastZones.equals(currentZones)) {

            pastZones.forEach(zone -> {
                if (!currentZones.contains(zone)) {//player left zone
                    PlayerLeaveZoneEvent e = new PlayerLeaveZoneEvent(player, zone);
                    Bukkit.getPluginManager().callEvent(e);
                    if (e.isCancelled()) {
                        Vector velocity = player.getLocation().getDirection();
                        velocity.setY(0.3);
                        player.setVelocity(velocity.multiply(-0.5));
                    }
                } else
                    currentZones.remove(zone);
            });
            for (ViceSelection entered : currentZones) {
                PlayerEnterZoneEvent e = new PlayerEnterZoneEvent(player, entered);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    Vector velocity = player.getLocation().getDirection();
                    velocity.setY(0.3);
                    player.setVelocity(velocity.multiply(-0.5));
                }
            }
        }

        if (!player.getWorld().getName().equalsIgnoreCase("spawn") && Vice.getWorldManager().getWarpManager().cancelTaxi(player, Vice.getUserManager().getLoadedUser(player.getUniqueId()))) {
            player.sendMessage(Lang.TAXI.f("&eYou moved! Teleportation cancelled!"));
            return;
        }

//        if (player.isGliding()) {
//            if (!player.isSneaking() || player.getLocation().getY() > 200) return;
//            double pitch = -event.getTo().getPitch();
//            if (pitch < 10 || pitch > 90) return;
//            Vector vector = player.getLocation().getDirection();
//            player.setVelocity(vector.multiply(1.6));
//            player.getWorld().playSound(event.getFrom(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 2.0F);
//            return;
//        }


    }
}
