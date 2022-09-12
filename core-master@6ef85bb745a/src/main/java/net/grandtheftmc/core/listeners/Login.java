package net.grandtheftmc.core.listeners;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.PluginAssociated;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.whitelist.WhitelistManager;
import net.grandtheftmc.core.whitelist.WhitelistedUser;

public class Login implements PluginAssociated, Listener {

	/** The owning plugin */
	private Plugin plugin;
	/** Maps uuid to user rank */
	private Map<UUID, UserRank> uuidToRank;

	/**
	 * Construct a new Login.
	 * <p>
	 * This is a listener that handles login related events.
	 * </p>
	 * 
	 * @param plugin - the owning plugin
	 */
	public Login(Plugin plugin) {
		this.plugin = plugin;
		this.uuidToRank = new HashMap<>();

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(Core.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.SETTINGS) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				String locale = event.getPacket().getStrings().read(0);
				User user = Core.getUserManager().getLoadedUser(event.getPlayer().getUniqueId());
				if (user == null)
					return;

				if (user.getLanguage() != null && user.getLanguage().equals(locale))
					return;
				user.setLanguage(locale);
			}
		});

		// every 60 secs clear the cache map
		Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), () -> {
			uuidToRank.clear();
		}, 0, 20L * 60);
	}

	/**
	 * Listens in on the async pre login event.
	 * 
	 * @param event - the event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

		// if the server is restarting
		if (Core.getInstance().isRestarting()) {
			event.setKickMessage(Lang.ALERTS.f("&cThe server is restarting!"));
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "&cThe server is currently restarting!");
			return;
		}

		// if the core plugin isn't enabled
		if (!Core.getInstance().isEnabled()) {
			event.setKickMessage(Lang.ALERTS.f("&cWaiting on core..."));
			event.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "&cWaiting on core...");
			return;
		}

		// grab event variables
		UUID uuid = event.getUniqueId();

		// if required rank to join
		if (Core.getSettings().needRankToJoin()) {

			// find the required rank to join
			UserRank requiredRank = Core.getSettings().getRankToJoin();
			UserRank userRank = UserRank.DEFAULT;

			// if cached rank
			if (uuidToRank.containsKey(uuid)) {
				userRank = uuidToRank.get(uuid);
			}
			// fetch from database
			else {

				// grab the user rank
				try (Connection conn = BaseDatabase.getInstance().getConnection()) {
					userRank = UserDAO.getHighestRank(conn, uuid);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				
				// add to cache
				uuidToRank.put(uuid, userRank);
			}

			// if they do not have the required rank OR HIGHER
			if (!userRank.isHigherThan(requiredRank) && userRank != requiredRank) {
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, Utils.f("&cSorry, this game is for " + requiredRank.getColoredNameBold() + " &c and up only! Go to " + Core.getSettings().getStoreLink() + " to purchase a rank!"));
			}
		}
	}

	/**
	 * Listens in the player login event.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {

		// grab event variables
		Player player = event.getPlayer();

		if (Core.getInstance().isRestarting()) {
			event.setKickMessage(Lang.ALERTS.f("&cThe server is restarting!"));
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			return;
		}

		// if the core plugin isn't enabled
		if (!Core.getInstance().isEnabled()) {
			event.setKickMessage(Lang.ALERTS.f("&cWaiting on core..."));
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "&cWaiting on core...");
			return;
		}

		// if whitelist is enabled
		WhitelistManager wm = Core.getWhitelistManager();
		if (wm.isEnabled()) {

			// grab the specified user
			WhitelistedUser wu = wm.getWhitelistedUser(player.getUniqueId());
			
			// if not found, try by name
			if (wu == null){
				wu = wm.getWhitelistedUser(player.getName());
			}

			// if we found the user, set the name/uuid
			if (wu != null) {
				wu.setName(player.getName());
				wu.setUuid(player.getUniqueId());
				
				// allow them to join
				return;
			}

			// if whitelist manager exists and there is no bypass rank specified
			if (wm != null && wm.getBypassRank() == null) {
				event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Utils.f("&cYou are not whitelisted on this server!"));
			}
			else {

				// find the required rank to join from whitelist
				UserRank requiredRank = wm.getBypassRank();
				UserRank userRank = UserRank.DEFAULT;

				// if cached rank
				if (uuidToRank.containsKey(player.getUniqueId())) {
					userRank = uuidToRank.get(player.getUniqueId());
				}
				// fetch from database
				else {

					// grab the user rank
					try (Connection conn = BaseDatabase.getInstance().getConnection()) {
						userRank = UserDAO.getHighestRank(conn, player.getUniqueId());
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}

				// if higher than whitelist rank
				if (userRank.hasRank(requiredRank)) {
					event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, Utils.f("&cSorry, this game is for " + wm.getBypassRank().getColoredNameBold() + "&c and up only! Go to " + Core.getSettings().getStoreLink() + " to purchase a rank!"));
				}
			}
		}
	}

	/**
	 * Get the owning plugin for this listener.
	 * 
	 * @return The owning plugin for this listener.
	 */
	@Override
	public Plugin getPlugin() {
		return plugin;
	}
}
