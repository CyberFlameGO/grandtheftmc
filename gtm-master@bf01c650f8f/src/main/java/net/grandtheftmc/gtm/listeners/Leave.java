package net.grandtheftmc.gtm.listeners;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.debug.Log;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.commands.SpectatorCommand;
import net.grandtheftmc.gtm.database.mutex.common.SaveGTMUserTask;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.PersonalVehicle;

public class Leave implements Listener {

	/**
	 * Listens in on player kick events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {

		// grab event variables
		Player p = event.getPlayer();

		GTMUser gtmUser = GTMUserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
		if (gtmUser != null) {
			gtmUser.setKicked(!event.getReason().contains("spam"));
		}
	}

	/**
	 * Listens in on player quit events.
	 * <p>
	 * Note: This is HIGH so that events can listen in BEFORE or AFTER this
	 * event is called since the player will be removed from the container.
	 * </p>
	 * 
	 * @param event - the event
	 */
	@EventHandler (priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event) {

		// grab event varialbes
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		
		// always set no quit message
		event.setQuitMessage(null);

		// grab gtm user
		GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
		Long leaveTime = System.currentTimeMillis();
		
		if (gtmUser != null){
			
			// if join time is specified, update playtime
			if (gtmUser.getJoinTime() != null) {
				Long playtimeSeconds = TimeUnit.MILLISECONDS.toSeconds(leaveTime - gtmUser.getJoinTime());
				gtmUser.setPlaytime(gtmUser.getPlaytime() + playtimeSeconds);
			}

			// remove vehicle
			if (gtmUser.getPersonalVehicle() != null && gtmUser.getPersonalVehicle().onMap() && !gtmUser.getPersonalVehicle().isStolen()) {
				gtmUser.getPersonalVehicle().getEntity().remove();
			}

			// if player is a spectator
			if (player.getGameMode() == GameMode.SPECTATOR) {
				player.getActivePotionEffects().clear();
				player.setFoodLevel(20);
				player.setGameMode(GameMode.ADVENTURE);
				player.setFlying(false);
				player.setFlySpeed(0.1F);
				if (gtmUser.isArrested()) {
					player.teleport(GTM.getWarpManager().getJail().getLocation());
				}
				else {
					player.teleport(GTM.getWarpManager().getSpawn().getLocation());
				}
				if (gtmUser.getJobMode() != JobMode.CRIMINAL) {
					gtmUser.setJobMode(JobMode.CRIMINAL);
				}
			}

			// grab location
			Location loc = player.getLocation();
			World world = loc.getWorld();

			// if player was in combat
			if (gtmUser.isInCombat() && !gtmUser.isKicked() && !"spawn".equalsIgnoreCase(world.getName())) {

				ItemStack[] contents = player.getInventory().getContents();
				player.getInventory().clear();

				for (ItemStack item : contents) {
					if (item != null && !(item.getType() == Material.WATCH || item.getType() == Material.COMPASS) && item.getType() != Material.WATCH && item.getType() != Material.CHEST) {
						world.dropItemNaturally(loc, item);
					}
				}
				player.getInventory().clear();

				User coreUser = UserManager.getInstance().getUser(uuid).orElse(null);
				if (coreUser != null) {
					Utils.broadcast(Lang.COMBATTAG + "&c" + coreUser.getColoredName(player) + "&7 has logged off during combat! All his items have been dropped on the ground.");

				}

				if (GTM.getWarpManager().getSpawn() != null) {
					player.teleport(GTM.getWarpManager().getSpawn().getLocation());
				}
			}

			if (gtmUser.hasPersonalVehicle()) {
				PersonalVehicle vehicle = gtmUser.getPersonalVehicle();
				if (vehicle.onMap()){
					vehicle.updateVehicleInDatabase(player, 0);
				}
			}

			if (SpectatorCommand.getActiveStaff().contains(player.getName())) {
				player.setGameMode(GameMode.ADVENTURE);
				SpectatorCommand.getActiveStaff().remove(player.getName());
			}

			// REMOVE from local container and save
			final GTMUser removedUser = GTMUserManager.getInstance().removeUser(player.getUniqueId()).orElse(null);
			if (removedUser != null) {

				new SaveGTMUserTask(GTM.getInstance(), removedUser) {
					@Override
					protected boolean onSave() {
						try (Connection conn = BaseDatabase.getInstance().getConnection()) {
							gtmUser.onSave(conn);
						}
						catch (SQLException e) {
							e.printStackTrace();
						}

						return true;
					}

					@Override
					protected void onSaveFailure() {
						Log.error("GTM", "Unhandled exception while saving " + removedUser.getName());
					}
				};
			}
		}
	}
}
