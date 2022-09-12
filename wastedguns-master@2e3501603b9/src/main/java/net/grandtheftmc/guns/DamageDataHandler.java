package net.grandtheftmc.guns;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageDataHandler {

	/** The singleton instance of this handler */
	private static DamageDataHandler instance;
	/** Maps uuid of entity to damage event cause */
	private Map<UUID, EntityDamageByEntityEvent> uuidToDamageEventCause;

	/**
	 * Construct a new DamageDataHandler.
	 * <p>
	 * This will be used to keep track of damage sources for damage events due
	 * to weapons.
	 * </p>
	 */
	private DamageDataHandler() {
		this.uuidToDamageEventCause = new HashMap<>();
	}

	/**
	 * Get the instance of this handler.
	 * 
	 * @return The instance of this handler.
	 */
	public static DamageDataHandler getInstance() {
		if (instance == null) {
			instance = new DamageDataHandler();
		}

		return instance;
	}

	/**
	 * Add damage data for the given uuid.
	 * 
	 * @param uuid - the uuid of the entity
	 * @param damageEvent - the damage event data to add
	 * 
	 * @return {@code true} if the data was overwritten, {@code false}
	 *         otherwise.
	 */
	public boolean addData(UUID uuid, EntityDamageByEntityEvent damageEvent) {

		boolean updated = false;
		if (uuidToDamageEventCause.containsKey(uuid)) {
			updated = true;
		}

		uuidToDamageEventCause.put(uuid, damageEvent);
		return updated;
	}

	/**
	 * Removes the damage data for the given uuid.
	 * 
	 * @param uuid - the uuid of the entity to remove data for
	 * 
	 * @return The data regarding the uuid damage, otherwise empty.
	 */
	public Optional<EntityDamageByEntityEvent> removeData(UUID uuid) {
		if (uuidToDamageEventCause.containsKey(uuid)) {
			return Optional.of(uuidToDamageEventCause.remove(uuid));
		}

		return Optional.empty();
	}

	/**
	 * Get the damage data for the given entity.
	 * 
	 * @param uuid - the uuid of the user to lookup
	 * 
	 * @return The damage data for the given entity, if exists.
	 */
	public Optional<EntityDamageByEntityEvent> getData(UUID uuid) {
		if (uuidToDamageEventCause.containsKey(uuid)) {
			return Optional.of(uuidToDamageEventCause.get(uuid));
		}

		return Optional.empty();
	}
}
