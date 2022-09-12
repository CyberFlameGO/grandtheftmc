package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedvehicles.api.events.JetpackFlyEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleDestroyEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleEnterEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehiclePassengerEnterEvent;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.PersonalVehicle;
import net.grandtheftmc.vice.users.ViceRank;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class VehicleUse implements Listener {

    @EventHandler
    public void onVehiclePassengerEnter(VehiclePassengerEnterEvent event) {
        if (Bukkit.getPlayer(event.getVehicle().getCreator()) == null) return;
        Player passenger = event.getPlayer();
        Player owner = Bukkit.getPlayer(event.getVehicle().getCreator());
        ViceUser passengerUser = Vice.getUserManager().getLoadedUser(passenger.getUniqueId());
        ViceUser ownerUser = Vice.getUserManager().getLoadedUser(owner.getUniqueId());
        // TODO replace the Gang check with a Cartel check :p
      /*  if (ownerUser.getGang() == null || passengerUser.getGang() == null) {
            passenger.sendMessage(Lang.VEHICLES.f("&fYou must be in the same gang as the Vehicle Driver to enter!"));
            event.setCancelled(true);
        } else
        if (ownerUser.getGang() != passengerUser.getGang()) {
            passenger.sendMessage(Lang.VEHICLES.f("&fYou must be in the same gang as the Vehicle Driver to enter!"));
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
        }*/

    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        Player player = e.getPlayer();
        if (Objects.equals("spawn", player.getWorld().getName())) {
            player.sendMessage(Lang.HEY.f("&7You can't enter vehicles in spawn!"));
            e.setCancelled(true);
            return;
        }
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (viceUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't enter vehicles in jail!"));
            e.setCancelled(true);
        }
        if (e.getVehicle().getCreator() == null) return;
        Player creator = Bukkit.getPlayer(e.getVehicle().getCreator());
        if (creator != null) {
            User u = Core.getUserManager().getLoadedUser(creator.getUniqueId());
            ViceUser user = Vice.getUserManager().getLoadedUser(creator.getUniqueId());
            PersonalVehicle vehicle = user.getPersonalVehicle();
            if (vehicle == null) return;
            if (Objects.equals(creator, player)) {
                if (!vehicle.isStolen()) return;
                vehicle.setStolen(false);
                player.sendMessage(Lang.VEHICLES.f("&7You recovered your stolen " + vehicle.getDisplayName() + "&7!"));
                return;
            }
            if (!Objects.equals(e.getArmorStand().getUniqueId(), vehicle.getEntityUUID()) || e.getArmorStand().getPassenger() != null || vehicle.isStolen())
                return;
            vehicle.setStolen(true);
            vehicle.updateVehicleInDatabase(creator, e.getArmorStand().getHealth());
            creator.sendMessage(Lang.VEHICLES.f("&7Your &c&l" + vehicle.getDisplayName() + "&7 was stolen!"));
            player.sendMessage(Lang.VEHICLES.f("&7You stole " + u.getColoredName(creator) + "&7's " + vehicle.getDisplayName() + "&7!"));
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent e) {
        Player creator = Bukkit.getPlayer(e.getVehicle().getCreator());
        if (creator != null) {
            ViceUser user = Vice.getUserManager().getLoadedUser(creator.getUniqueId());
            PersonalVehicle vehicle = user.getPersonalVehicle();
            if (vehicle == null || !e.getVehicle().getVehicleProperties().getIdentifier().equalsIgnoreCase(vehicle.getVehicle()))
                return;
            vehicle.updateVehicleInDatabase(creator, 0);
            creator.sendMessage(Lang.VEHICLES.f("&7Your &c&l" + vehicle.getDisplayName() + "&7 was destroyed!"));
        }
    }

    @EventHandler
    public void onJetpackFly(JetpackFlyEvent e) {
        Player p = e.getPlayer();
        ViceUser user = Vice.getUserManager().getLoadedUser(e.getPlayer().getUniqueId());
        if (p.isSprinting() && p.isFlying()) {
            p.setSprinting(false);
        }
        if (p.getWorld().getName().equalsIgnoreCase("spawn")) {
            if (user.getLastJetpackCancel() + 2000 < System.currentTimeMillis()) {
                user.setLastJetpackCancel(System.currentTimeMillis());
                e.getPlayer().sendMessage(Lang.VEHICLES.f("&7You cannot fly in the spawn area!"));
                e.setCancelled(true);
            }
            return;
        }

        if (!user.isRank(ViceRank.DRUGLORD) && !Core.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()).isRank(UserRank.SPONSOR)) {
            e.setCancelled(true);
            if (user.getLastJetpackCancel() + 2000 < System.currentTimeMillis()) {
                user.setLastJetpackCancel(System.currentTimeMillis());
                e.getPlayer().sendMessage(Lang.VEHICLES.f("&7You need to rank up to " + ViceRank.DRUGLORD.getColoredNameBold() + "&7 or donate for " + UserRank.SPONSOR.getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use the jetpack!"));
            }
        }
    }
}