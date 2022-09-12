package net.grandtheftmc.gtm.listeners;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity. Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.voting.Reward;
import net.grandtheftmc.core.voting.events.RewardCheckEvent;
import net.grandtheftmc.core.voting.events.RewardGiveEvent;
import net.grandtheftmc.core.voting.events.RewardInfoEvent;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.CheatCodeState;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;

public class VoteReward implements Listener {

    @EventHandler
    public void rewardGiveEvent(RewardGiveEvent event) {
        Player player = event.getPlayer();
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        String identifier = event.getIdentifier();
        switch (event.getRewardType()) {
            case VEHICLE: {
                Optional<VehicleProperties> vehicleOptional = GTM.getWastedVehicles().getVehicle(identifier);
                if (!vehicleOptional.isPresent()) return;
                gtmUser.giveVehiclePerm(player, vehicleOptional.get());
                player.sendMessage(Lang.REWARDS.f("&4&l" + identifier));
                break;
            }
            case CHEATCODE: {
                CheatCode code = CheatCode.valueOf(identifier.toUpperCase());
                if(gtmUser.getCheatCodeState(code).getState()== State.LOCKED) {
                    gtmUser.setCheatCodeState(code, new CheatCodeState(code.getDefaultState(), false));
                    player.sendMessage(Lang.REWARDS.f("&a&l+ " + code.toString() + " CHEATCODE"));
                }
                else
                    player.sendMessage(Lang.REWARDS.f("&cYou recieved the " + identifier + " cheatcode, but you already have it."));
                break;
            }
            // Note: give event for weapons is handled in UpdateListener
        }

    }

    @EventHandler
    public void rewardCheckEvent(RewardCheckEvent event) {
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(event.getPlayer().getUniqueId());
        String identifier = event.getIdentifier();
        switch (event.getRewardType()) {
            case VEHICLE:
                Optional<VehicleProperties> vehicleOptional = GTM.getWastedVehicles().getVehicle(identifier);
                if (!vehicleOptional.isPresent()) return;
                event.setResult(gtmUser.hasVehicle(identifier));
                break;
            case CHEATCODE:
                CheatCode code = CheatCode.valueOf(identifier.toUpperCase());
                event.setResult(gtmUser.getCheatCodeState(code).getState()!=State.LOCKED);
                break;
        }
    }

    @EventHandler
    public void rewardInfoEvent(RewardInfoEvent event) {
    	
    	Reward reward = event.getReward();
        String identifier = event.getIdentifier();
        
        switch (event.getRewardType()) {
            case VEHICLE:
                Optional<VehicleProperties> vehicleOptional = GTM.getWastedVehicles().getVehicle(identifier);
                if (!vehicleOptional.isPresent()) return;
                event.setDisplayItem(vehicleOptional.get().getItem());
                break;
            case CHEATCODE:
                CheatCode code = CheatCode.valueOf(identifier.toUpperCase());
                event.setDisplayItem(code.getDisplayItem(null, State.LOCKED));
                break;
            case WEAPON:
            	
            	Weapon weapon = GTMGuns.getInstance().getWeaponManager().getWeapon(reward.getName()).orElse(null);
            	
            	// if reward object is here
            	if (GTMGuns.STAR_SYSTEM){
            		int stars = 1;
            		if (reward != null){
            			stars = reward.getStars();
            		}
            		
            		event.setDisplayItem(weapon.createItemStack(stars, null));
            	}
            	else{
            		event.setDisplayItem(weapon.createItemStack());
            	}
                break;
            case SKIN:
                ItemStack stack = new ItemStack(Material.ENCHANTED_BOOK);
                ItemMeta meta = stack.getItemMeta();

                if (event.getIdentifier().equals("weapon_skin_common")) {
                    meta.setDisplayName(Utils.f("&9&lCommon Weapon Skin"));
                } else if (event.getIdentifier().equals("weapon_skin_rare")) {
                    meta.setDisplayName(Utils.f("&9&lRare Weapon Skin"));
                } else if (event.getIdentifier().equals("weapon_skin_epic")) {
                    meta.setDisplayName(Utils.f("&9&lEpic Weapon Skin"));
                } else if (event.getIdentifier().equals("weapon_skin_legendary")) {
                    meta.setDisplayName(Utils.f("&9&lLegendary Weapon Skin"));
                }

                stack.setItemMeta(meta);
                event.setDisplayItem(stack);
                
                break;
        }
    }
}
