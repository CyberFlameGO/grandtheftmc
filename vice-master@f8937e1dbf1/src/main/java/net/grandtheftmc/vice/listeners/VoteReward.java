package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.voting.Reward;
import net.grandtheftmc.core.voting.events.RewardCheckEvent;
import net.grandtheftmc.core.voting.events.RewardGiveEvent;
import net.grandtheftmc.core.voting.events.RewardInfoEvent;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class VoteReward implements Listener {

    @EventHandler
    public void rewardGiveEvent(RewardGiveEvent event) {
        Player player = event.getPlayer();
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        String identifier = event.getIdentifier();
        if (event.getRewardType() == Reward.RewardType.VEHICLE) {
            Optional<VehicleProperties> vehicleOptional = Vice.getWastedVehicles().getVehicle(identifier);
            if (!vehicleOptional.isPresent()) return;
            viceUser.giveVehiclePerm(player, vehicleOptional.get());
            player.sendMessage(Lang.REWARDS.f("&4&l" + identifier));
        }
    }

    @EventHandler
    public void rewardCheckEvent(RewardCheckEvent event) {
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(event.getPlayer().getUniqueId());
        String identifier = event.getIdentifier();
        if (event.getRewardType() == Reward.RewardType.VEHICLE) {
            Optional<VehicleProperties> vehicleOptional = Vice.getWastedVehicles().getVehicle(identifier);
            if (!vehicleOptional.isPresent()) return;
            event.setResult(viceUser.hasVehicle(identifier));
        }
    }

    @EventHandler
    public void rewardInfoEvent(RewardInfoEvent event) {
        String identifier = event.getIdentifier();
        if (event.getRewardType() == Reward.RewardType.VEHICLE) {
            Optional<VehicleProperties> vehicleOptional = Vice.getWastedVehicles().getVehicle(identifier);
            if (!vehicleOptional.isPresent()) return;
            event.setDisplayItem(vehicleOptional.get().getItem());
        }
    }
}
