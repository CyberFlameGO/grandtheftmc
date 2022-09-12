package net.grandtheftmc.core.resourcepack;

import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketContainer;

/**
 * Created by Luke Bingham on 07/08/2017.
 */
public interface ResourcePackReceiving {
	
	/**
	 * Callback for when a packet is received.
	 * 
	 * @param manager - the resource pack manager container
	 * @param packet - the packet container being sent
	 * @param player - the player involved in the event
	 */
    void onReceiving(ResourcePackManager manager, PacketContainer packet, Player player);
}
