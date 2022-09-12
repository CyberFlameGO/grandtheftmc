package net.grandtheftmc.core.resourcepack;

import org.bukkit.entity.Player;

import net.grandtheftmc.core.events.CoreEvent;

/**
 * Created by Luke Bingham on 07/08/2017.
 */
public class ResourcePackEvent extends CoreEvent {

	/** The player involved in the event */
	private final Player player;
	/** The resource status constant associated with the event */
	private final ResourceStatus status;

	/**
	 * Construct a new Event
	 */
	public ResourcePackEvent(Player player, ResourceStatus status) {
		super(false);
		this.player = player;
		this.status = status;
	}

	/**
	 * Get the player involved in the event.
	 * 
	 * @return The player involved in the event.
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Get the status of the resource pack involved in the event/
	 * 
	 * @return The status of the resource pack.
	 */
	public ResourceStatus getStatus() {
		return this.status;
	}

	/**
	 * Enum constant payloads that are received by the player client.
	 */
	public enum ResourceStatus {
		SUCCESSFULLY_LOADED, DECLINED, FAILED_DOWNLOAD, ACCEPTED, NO_RESPONSE;
	}
}
