package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.commands.SpectatorCommand;
import net.grandtheftmc.vice.users.PersonalVehicle;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Leave implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        ViceUser user = Vice.getUserManager().getLoadedUser(e.getPlayer().getUniqueId());
        user.setBooleanToStorage(BooleanStorageType.KICKED, true);
        Vice.getCombatLogManager().spawnNPC(player, player.getWorld().getName().equals("spawn"), e.getReason().contains("spam"));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
        Long leaveTime = System.currentTimeMillis();
        Long playtimeSeconds = TimeUnit.MILLISECONDS.toSeconds(leaveTime - user.getJoinTime());
        user.setPlaytime(user.getPlaytime() + playtimeSeconds);
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.getActivePotionEffects().clear();
            player.setFoodLevel(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setFlying(false);
            player.setFlySpeed(0.1F);
            if (user.isArrested()) {
                player.teleport(Vice.getWorldManager().getWarpManager().getJail().getLocation());
            } else {
                player.teleport(Vice.getWorldManager().getWarpManager().getSpawn().getLocation());
            }
        }
        Location loc = player.getLocation();
        User u = Core.getUserManager().getLoadedUser(uuid);
//        u.removeCosmetics(player, CosmeticType.HAT);
        if (user.hasPersonalVehicle()) {
            PersonalVehicle vehicle = user.getPersonalVehicle();
            if (vehicle.onMap())
                vehicle.updateVehicleInDatabase(player, 0);
        }
        if (SpectatorCommand.getActiveStaff().contains(player.getName())) {
            player.setGameMode(GameMode.SURVIVAL);
            SpectatorCommand.getActiveStaff().remove(player.getName());
        }
        Vice.getCombatLogManager().spawnNPC(player, player.getWorld().getName().equals("spawn"));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeaveMonitor(PlayerQuitEvent e) {
        Vice.getUserManager().unloadUser(e.getPlayer().getUniqueId());
    }

}
