package net.grandtheftmc.hub.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.mutex.Mutexable;
import net.grandtheftmc.core.database.mutex.event.MutexLoadCompleteEvent;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.hub.Hub;
import net.grandtheftmc.hub.HubUtils;
import us.myles.ViaVersion.api.Via;

public class Join implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		// grab event variables
		Player player = e.getPlayer();

		// see below method
	}

	@EventHandler
	public void onMutexLoadComplete(MutexLoadCompleteEvent event) {

		// grab event variables
		Mutexable mutexable = event.getMutexable();
		if (mutexable instanceof User) {
			User user = (User) mutexable;

			// make sure still online
			Player player = Bukkit.getPlayer(user.getUUID());
			if (player != null) {

				HubUtils.sendJoinMessage(player, user);
				HubUtils.giveItems(player);
				HubUtils.updateBoard(player, user);
				user.setPref(player, Pref.PLAYERS_SHOWN, true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));

				if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
					int playerVersion = Via.getAPI().getPlayerVersion(player.getUniqueId());
					if (playerVersion < 210) {
						player.sendMessage(Lang.HEY.f("&7It appears you're using an older version of Minecraft. " + "It is highly recommended you use the latest Minecraft version for the best experience!"));
					}
				}

				if (Hub.getInstance().getSpawn() != null) {
					if (Hub.getInstance().getSpawnPoints().isEmpty()) {
						player.teleport(Hub.getInstance().getSpawn());
						return;
					}
					int size = Hub.getInstance().getSpawnPoints().size();
					player.teleport((Location) Hub.getInstance().getSpawnPoints().toArray()[new Random().nextInt(size)]);
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
		if (!user.hasMoved()) {
			user.setHasMoved();
			user.updateVisibility(player);
		}
	}
}
