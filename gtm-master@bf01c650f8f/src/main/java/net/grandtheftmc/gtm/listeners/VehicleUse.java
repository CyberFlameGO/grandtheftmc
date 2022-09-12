package net.grandtheftmc.gtm.listeners;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.inventory.ItemStack;

import com.j0ach1mmall3.wastedvehicles.api.events.JetpackFlyEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleDestroyEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleEnterEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehiclePassengerEnterEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.PersonalVehicle;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;

public class VehicleUse implements Listener {

    @EventHandler
    public void onVehiclePassengerEnter(VehiclePassengerEnterEvent event) {
        if (Bukkit.getPlayer(event.getVehicle().getCreator()) == null) return;
        Player passenger = event.getPlayer();
        Player owner = Bukkit.getPlayer(event.getVehicle().getCreator());
        
        GTMUser passengerUser = GTMUserManager.getInstance().getUser(passenger.getUniqueId()).orElse(null);
        GTMUser ownerUser = GTMUserManager.getInstance().getUser(owner.getUniqueId()).orElse(null);

        Optional<Gang> ownerOpt = GangManager.getInstance().getGangByMember(owner.getUniqueId()), passengerOpt = GangManager.getInstance().getGangByMember(passenger.getUniqueId());

        if (!ownerOpt.isPresent() || !passengerOpt.isPresent()) {
            passenger.sendMessage(Lang.VEHICLES.f("&fYou must be in the same gang as the Vehicle Driver to enter!"));
            event.setCancelled(true);
        } else {
            if (ownerOpt.get() != passengerOpt.get()) {
                passenger.sendMessage(Lang.VEHICLES.f("&fYou must be in the same gang as the Vehicle Driver to enter!"));
                event.setCancelled(true);
                return;
            }

            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e) {
        Player player = e.getPlayer();
        if (Objects.equals("spawn", player.getWorld().getName())) {
            player.sendMessage(Lang.HEY.f("&7You can't enter vehicles in spawn!"));
            e.setCancelled(true);
            return;
        }
        
        GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        
        if (gtmUser != null){
            if (gtmUser.isArrested()) {
                player.sendMessage(Lang.JAIL.f("&7You can't enter vehicles in jail!"));
                e.setCancelled(true);
            }
        }
        
        if (e.getVehicle().getCreator() == null) return;
        Player creator = Bukkit.getPlayer(e.getVehicle().getCreator());
        if (creator != null) {
            User u = UserManager.getInstance().getUser(creator.getUniqueId()).orElse(null);
            GTMUser user = GTMUserManager.getInstance().getUser(creator.getUniqueId()).orElse(null);
            
            if (u != null && user != null){
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
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent e) {
    	
    	// grab event variables
        Player creator = Bukkit.getPlayer(e.getVehicle().getCreator());
        
        if (creator != null) {
        	
            GTMUser user = GTMUserManager.getInstance().getUser(creator.getUniqueId()).orElse(null);
            if (user != null){
                PersonalVehicle vehicle = user.getPersonalVehicle();
                if (vehicle == null || !e.getVehicle().getVehicleProperties().getIdentifier().equalsIgnoreCase(vehicle.getVehicle()))
                    return;
                vehicle.updateVehicleInDatabase(creator, 0);
                creator.sendMessage(Lang.VEHICLES.f("&7Your &c&l" + vehicle.getDisplayName() + "&7 was destroyed!"));
            }
        }
    }

    @EventHandler
    public void onJetpackFly(JetpackFlyEvent e) {
    	
    	// grab event variables
        Player p = e.getPlayer();
        GTMUser user = GTMUserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
        
        if (p.isSprinting() && p.isFlying()) {
            p.setSprinting(false);
        }

        if (user != null){
            if (user.getJobMode() == JobMode.COP) {
                e.setCancelled(true);
                
                if (p.getInventory().getChestplate() != null){
                	ItemStack chestItem = p.getInventory().getChestplate();

                	if (chestItem.getType() == Material.ELYTRA || chestItem.getType() == Material.GOLD_CHESTPLATE) {
                		Utils.giveItems(p, p.getInventory().getChestplate());
                        p.getInventory().setChestplate(null);
                	}
                }
                
                if (user.getLastJetpackCancel() + 2000 < System.currentTimeMillis()) {
                    user.setLastJetpackCancel(System.currentTimeMillis());
                    e.getPlayer().sendMessage(Lang.VEHICLES.f("&7Jetpacks cannot be used by Cops."));
                    e.getPlayer().setAllowFlight(false);
                    e.getPlayer().setFlying(false);
                }
                return;
            }
            if(!user.canUseJetpack()){
                e.setCancelled(true);
                if (user.getLastJetpackCancel() + 2000 < System.currentTimeMillis()) {
                    user.setLastJetpackCancel(System.currentTimeMillis());
                    e.getPlayer().sendMessage(Lang.VEHICLES.f("&7Your jetpack was disabled! Please wait &a&l"+Utils.timeInMillisToText(user.getEnableJetpackTime()-System.currentTimeMillis())+"&7 to start flying again."));
                    e.getPlayer().setAllowFlight(false);
                    e.getPlayer().setFlying(false);
                }
                return;
            }

            User coreUser = UserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
            if (coreUser != null){
                if (!user.isRank(GTMRank.MOBSTER) && !coreUser.isRank(UserRank.SPONSOR)) {
                    e.setCancelled(true);
                    
                    ItemStack chestPlate = p.getInventory().getChestplate();
                    if (chestPlate != null) {
                    	if (chestPlate.getType() == Material.ELYTRA || chestPlate.getType() == Material.GOLD_CHESTPLATE){
                            Utils.giveItems(p, p.getInventory().getChestplate());
                            p.getInventory().setChestplate(null);
                    	}
                    }

                    if (user.getLastJetpackCancel() + 2000 < System.currentTimeMillis()) {
                        user.setLastJetpackCancel(System.currentTimeMillis());
                        e.getPlayer().sendMessage(Lang.VEHICLES.f("&7You need to rank up to " + GTMRank.MOBSTER.getColoredNameBold() + "&7 or donate for " + UserRank.SPONSOR.getColoredNameBold() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7 to use the jetpack!"));
                        e.getPlayer().setAllowFlight(false);
                        e.getPlayer().setFlying(false);
                    }
                }
            }
        }
        
        // get house user
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(p.getUniqueId());
        if (houseUser != null){
        	
        	// are they inside a house
        	if (houseUser.isInsideHouse()){
        		
        		// if they have the jetpack on, unequip
        		if (p.getInventory().getChestplate()!=null && p.getInventory().getChestplate().getType() == Material.ELYTRA
                        || p.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE) {
                    Utils.giveItems(p, p.getInventory().getChestplate());
                    p.getInventory().setChestplate(null);
                }
        		
        		if (user.getLastJetpackCancel() + 2000 < System.currentTimeMillis()) {
                    user.setLastJetpackCancel(System.currentTimeMillis());
                    e.getPlayer().sendMessage(Lang.VEHICLES.f("&fYou cannot use a &4jetpack &fin houses!"));
                    e.getPlayer().setAllowFlight(false);
                    e.getPlayer().setFlying(false);
                }
        	}
        }
    }
    
    @EventHandler
    public void onPlayerFly(EntityToggleGlideEvent event){
    	
    	Entity entity = event.getEntity();
    	if (!(entity instanceof Player)){
    		return;
    	}
    	
    	Player p = (Player) entity;
        GTMUser user = GTMUserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
        
        if (user != null){
        	
            if (user.getJobMode() == JobMode.COP) {
                event.setCancelled(true);
                
                if (p.getInventory().getChestplate()!=null && p.getInventory().getChestplate().getType() == Material.ELYTRA
                        || p.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE) {
                    Utils.giveItems(p, p.getInventory().getChestplate());
                    p.getInventory().setChestplate(null);
                }
                
                if (user.getLastJetpackCancel() + 2000 < System.currentTimeMillis()) {
                    user.setLastJetpackCancel(System.currentTimeMillis());
                    p.sendMessage(Lang.VEHICLES.f("&7Wingsuits and/or Jetpacks cannot be used by Cops."));
                    p.setAllowFlight(false);
                    p.setFlying(false);
                }
                
                return;
            }
        }
    }
}