package net.grandtheftmc.core.resourcepack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.google.common.collect.Maps;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.Callback;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.title.NMSTitle;

/**
 * Created by Luke Bingham on 06/08/2017.
 */
public class ResourcePackManager implements Component<ResourcePackManager, Core>, PacketListener {

	/** Mapping of versions to associated resource packs */
	private final HashMap<NMSVersion, ResourcePack> resourcePacks;
	/** Interface implementation of receiving client handling per version */
	private final ResourcePackReceiving recoursePackReceiving;
	/** NMS title to display to user */
	private final NMSTitle nmsTitle;

	/**
	 * Construct a new ResourcePackManager.
	 * 
	 * @param plugin - the owning plugin
	 * @param resourcePackReceiving - the interface handler for handling of the
	 *            resource pack
	 * @param nmsTitle - the title object to send
	 */
	public ResourcePackManager(final JavaPlugin plugin, final ResourcePackReceiving recoursePackReceiving, final NMSTitle nmsTitle) {
		this.resourcePacks = Maps.newHashMap();
		this.recoursePackReceiving = recoursePackReceiving;
		this.nmsTitle = nmsTitle;

		Bukkit.getPluginManager().registerEvents(this, plugin);
		ProtocolLibrary.getProtocolManager().addPacketListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResourcePackManager onDisable(Core plugin) {
		this.resourcePacks.clear();
		return this;
	}

	/**
	 * Get the resource pack with the associated version.
	 * 
	 * @param version - the client version
	 * 
	 * @return The resource pack that is associated with the specified version.
	 */
	public ResourcePack getResourcePack(NMSVersion version) {
		return this.resourcePacks.getOrDefault(version, null);
	}

	/**
	 * Set the resource pack and version key/pair.
	 * 
	 * @param version - the version key
	 * @param resourcePack - the resource pack value
	 */
	public void setResourcePack(NMSVersion version, ResourcePack resourcePack) {
		this.resourcePacks.put(version, resourcePack);
	}

	/**
	 * Get whether or not, via the callback, if we can send the resource pack to
	 * the specified player.
	 * 
	 * @param player - the player in question
	 * @param callback - the callback object
	 */
	public void canSendPack(Player player, Callback<Boolean> callback) {
		ServerUtil.runTaskAsync(() -> {
			try (Connection connection = BaseDatabase.getInstance().getConnection()) {
				try (PreparedStatement statement = connection.prepareStatement("SELECT last_pack FROM user_respack WHERE uuid=UNHEX(?);")) {
					statement.setString(1, player.getUniqueId().toString().replaceAll("-", ""));

					try (ResultSet result = statement.executeQuery()) {
						if (result.next()) {
							String pack = result.getString(1);
							if (!pack.equals(Core.getSettings().getType().name())) {
								callback.call(true);

								try (PreparedStatement update = connection.prepareStatement("UPDATE user_respack SET last_pack=? WHERE uuid=UNHEX(?);")) {
									update.setString(1, Core.getSettings().getType().name());
									update.setString(2, player.getUniqueId().toString().replaceAll("-", ""));

									update.execute();
								}
								return;
							}
						}
						else {
							callback.call(true);
							try (PreparedStatement insert = connection.prepareStatement("INSERT INTO user_respack (uuid, last_pack) VALUES (UNHEX(?), ?);")) {
								insert.setString(1, player.getUniqueId().toString().replaceAll("-", ""));
								insert.setString(2, Core.getSettings().getType().name());

								insert.execute();
							}
							return;
						}
					}
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}

			callback.call(false);
		});
	}

	/**
	 * Display a declined message.
	 * 
	 * @param player - the player getting the message
	 */
	protected void sendDeclined(Player player) {
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage(Utils.f("&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀"));
		player.sendMessage("");
		player.sendMessage(Utils.f(" &c&lRESPACK&4&l> &7Please go into your client's server list and make sure &aServer Resource Packs&7 is set to &aenabled&7."));
		player.sendMessage("");
		player.sendMessage(Utils.f("&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀&c▔&4▀"));

//        player.sendMessage(Lang.VICE.f("&7Download the server resource pack here: &a" + url));
//        player.sendMessage(Lang.VICE.f("&7or automatically apply it later using &a/respack"));
		NMSTitle.sendTitle(player, Utils.f("&c&lDownload denied!"), Utils.f("&7Please check your server resourcepack settings."), 0, 2 * 20, 1 * 20);
	}

	/**
	 * Display a failed message.
	 * 
	 * @param player - the player getting the message
	 */
	protected void sendFailed(Player player) {
		player.sendMessage(Utils.f(" &a&lRESPACK&8&l> &cThe Resource Pack Failed to download.."));
		player.sendMessage(Utils.f(" &a&lRESPACK&8&l> &cMake sure you're using an official minecraft build."));
//        player.sendMessage(Lang.VICE.f("&7Download the server resource pack here: &a" + url));
//        player.sendMessage(Lang.VICE.f("&7or automatically apply it later using &a/respack"));
		NMSTitle.sendTitle(player, Utils.f("&4&lRESOURCE PACK"), Utils.f("&cThe Resource Pack Failed to download!"), 0, 2 * 20, 1 * 20);
	}

	/**
	 * Display a loading title.
	 * 
	 * @param player - the player getting the title
	 */
	protected void sendLoading(Player player) {
		NMSTitle.sendTitle(player, Utils.f("&4&lRESOURCE PACK"), Utils.f("&fThis could freeze your client for a few seconds."), 0, 2 * 20, 1 * 20);
	}

	/**
	 * Display a loading complete title.
	 * 
	 * @param player - the player getting the title
	 */
	protected void sendLoaded(Player player) {
		NMSTitle.sendTitle(player, Utils.f("&4&lRESOURCE PACK"), Utils.f("&fLoading complete!"), 0, 2 * 20, 1 * 20);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPacketSending(PacketEvent packetEvent) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPacketReceiving(PacketEvent packetEvent) {
		recoursePackReceiving.onReceiving(this, packetEvent.getPacket(), packetEvent.getPlayer());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return ListeningWhitelist.EMPTY_WHITELIST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return ListeningWhitelist.newBuilder().normal().gamePhase(GamePhase.PLAYING).types(PacketType.Play.Client.RESOURCE_PACK_STATUS).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Plugin getPlugin() {
		return Core.getInstance();
	}
}
