package net.grandtheftmc.gtm.event.easter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public class EasterPacketListener implements PacketListener {

	private final Plugin plugin;
	private final EasterEvent event;

	public EasterPacketListener(Plugin plugin, EasterEvent event) {
		this.plugin = plugin;
		this.event = event;
	}

	/**
	 * Invoked right before a packet is transmitted from the server to the client.
	 * <p>
	 * Note that the packet may be replaced, if needed.
	 * <p>
	 * This method is executed on the main thread in 1.6.4 and earlier, and thus the Bukkit API is safe to use.
	 * <p>
	 * In Minecraft 1.7.2 and later, this method MAY be executed asynchronously, but only if {@link ListenerOptions#ASYNC}
	 * have been specified in the listener. This is off by default.
	 *
	 * @param event - the packet that should be sent.
	 */
	@Override
	public void onPacketSending(PacketEvent event) {
		if (event.getPacketType() == PacketType.Play.Server.SPAWN_ENTITY) {
			int id = event.getPacket().getIntegers().readSafely(0);
			if (!this.event.eggEntityIds.containsKey(id)) return;

			EasterEgg easterEgg = this.event.eggEntityIds.get(id);
			if (easterEgg == null) return;

			if (!this.event.playerCache.containsKey(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
				return;
			}

			EasterPlayerData playerData = this.event.playerCache.get(event.getPlayer().getUniqueId());
			if (playerData.hasFoundEgg(easterEgg.getUniqueIdentifier())) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Invoked right before a received packet from a client is being processed.
	 * <p>
	 * <b>WARNING</b>: <br>
	 * This method will be called <i>asynchronously</i>! You should synchronize with the main
	 * thread using {@link BukkitScheduler#scheduleSyncDelayedTask(Plugin, Runnable, long) scheduleSyncDelayedTask}
	 * if you need to call the Bukkit API.
	 *
	 * @param event - the packet that has been received.
	 */
	@Override
	public void onPacketReceiving(PacketEvent event) {
		//404
	}

	/**
	 * Retrieve which packets sent by the server this listener will observe.
	 *
	 * @return List of server packets to observe, along with the priority.
	 */
	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return ListeningWhitelist.newBuilder().types(PacketType.Play.Server.SPAWN_ENTITY).build();
	}

	/**
	 * Retrieve which packets sent by the client this listener will observe.
	 *
	 * @return List of server packets to observe, along with the priority.
	 */
	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return ListeningWhitelist.EMPTY_WHITELIST;
	}

	/**
	 * Retrieve the plugin that created list packet listener.
	 *
	 * @return The plugin, or NULL if not available.
	 */
	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}
}
