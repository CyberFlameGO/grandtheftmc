package net.grandtheftmc.core.stat;

import java.sql.Connection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.UUIDUtil;
import net.minecraft.server.v1_12_R1.PacketHandshakingInSetProtocol;

public class StatFactory {

	/** The instance of this factory */
	private static StatFactory instance;
	/** Whether or not the stat factory was initialized */
	private static boolean initialized;

	/** The owning plugin */
	private Plugin plugin;

	/**
	 * Construct a new StatFactory.
	 * <p>
	 * This represents a data structure that will collate and save statistical
	 * information.
	 * 
	 * @param plugin - the owning plugin
	 */
	private StatFactory(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Get the instance of this factory.
	 * 
	 * @return The singleton instance for this factory.
	 * 
	 * @throws IllegalStateException if the factory was never initialized with
	 *             {@link #init(Plugin)}.
	 */
	public static StatFactory getInstance() throws IllegalStateException {
		if (instance == null) {
			if (!initialized) {
				throw new IllegalStateException("The StatFactory was never initialized by the owning plugin! This is a severe error and should be fixed. Please call StatFactory.init() first!");
			}
		}

		return instance;
	}

	/**
	 * Initialize the manager.
	 * 
	 * @param plugin - the owning plugin
	 */
	public static void init(Plugin plugin) {
		
		System.out.println("[StatFactory] Initializing stat factory...");

		// create singleton instance
		instance = new StatFactory(plugin);
		initialized = true;
	}

	/**
	 * Register the client connection stat listener.
	 * <p>
	 * This is used to keep track of what IP players are connecting through.
	 * </p>
	 * 
	 * @param protocolManager - the protocol lib manager.
	 * 
	 * @return {@code true} if the client was registered, {@code false}
	 *         otherwise.
	 */
	public boolean registerClientConnectionStat(ProtocolManager protocolManager) {
		if (!initialized) {
			return false;
		}
		
		System.out.println("[StatFactory] Registering client connection stat...");

		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Handshake.Client.SET_PROTOCOL) {

			@Override
			public void onPacketReceiving(PacketEvent event) {

				// confirm correct packet
				if (event.getPacketType() == PacketType.Handshake.Client.SET_PROTOCOL) {
					
					Player p = event.getPlayer();
					if (p != null) {
						
						// get the handshake packet
						PacketHandshakingInSetProtocol packet = (PacketHandshakingInSetProtocol) event.getPacket().getHandle();

						// hostname field from packet is 255 bytes long
						// in form of hostname:
						// dev.mc-gtm.net24.101.17.184b50fd86cbcc642d0a5f87e975f5a817c
						// THEN SOME RANDOM ASS JSON
						String hostname = packet.hostname;

						// drop the JSON attached info
						String[] parts = hostname.split("\\[");
						if (parts.length > 1) {
							
							// in form of
							// dev.mc-gtm.net24.100.10.100b50fd86cbcc642d0a5f87e975f5a817c
							String playerInfo = parts[0];

							// split on a regex of IP format
							String[] playerParts = playerInfo.split("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}");

							if (playerParts.length > 1) {

								// IP the player CONNECTED TO
								String connectedIP = playerParts[0];
								// player uuid
								String trimmedUUID = playerParts[1];
								
								// TODO remove debug
								Core.log("[StatFactory][DEBUG] uuid=" + trimmedUUID + " conn using " + connectedIP);

								// if we correctly parsed the connected ip
								if (connectedIP != null && !connectedIP.isEmpty()) {
									
									// if this is because of SRV records
									if (connectedIP.contains("gtm-prox1") || connectedIP.contains("gtm-prox2")){
										connectedIP = "mc-gtm.net";
									}
									if (connectedIP.contains("gtm-dream") || connectedIP.contains("dream.mc-gtm")){
										connectedIP = "dream.mc-gtm.net";
									}
									if (connectedIP.contains("gtm-vicemc") || connectedIP.contains("vicemc.net")){
										connectedIP = "play.vicemc.net";
									}

									// if uuid is correctly parsed
									if (trimmedUUID != null && !trimmedUUID.isEmpty()) {

										// remove escapes and whitespace
										trimmedUUID = trimmedUUID.replace("\\", "");
										trimmedUUID = trimmedUUID.trim();

										UUID userUUID = UUIDUtil.createUUID(trimmedUUID).orElse(null);
										if (userUUID != null){
											
											String finalConnectedIP = connectedIP;
											
											// TODO remove debug
											Core.log("[StatFactory][DEBUG] Adding to db uuid=" + trimmedUUID + " conn using " + finalConnectedIP);
											
											Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
												try (Connection conn = BaseDatabase.getInstance().getConnection()){
													StatDAO.createUserJoinInfo(conn, userUUID, finalConnectedIP);
												}
												catch(Exception e){
													e.printStackTrace();
												}
											});
										}
									}
								}
							}
						}
					}
				}
			}
		});

		return true;
	}
}
