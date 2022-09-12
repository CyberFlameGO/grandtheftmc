package net.grandtheftmc.gtm.event.easter;

import net.grandtheftmc.core.wrapper.packet.out.WrapperPlayServerEntityDestroy;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public final class EasterEgg {

	/** Unique Identifier of the Egg */
	private int uniqueIdentifier;
	/** Location of the Egg block */
	private final Location location;

	private ArmorStand armorStand;

	protected EasterEgg(Location location) {
		this.location = location;
	}

	protected EasterEgg(int uniqueIdentifier, Location location) {
		this.uniqueIdentifier = uniqueIdentifier;
		this.location = location;
	}

	protected int getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	protected void setUniqueIdentifier(int uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	protected Location getLocation() {
		return location;
	}

	protected ArmorStand getArmorStand() {
		return armorStand;
	}

	protected void setArmorStand(ArmorStand armorStand) {
		this.armorStand = armorStand;
	}

	protected void destroyFor(Player player) {
		if (this.armorStand == null) return;

		WrapperPlayServerEntityDestroy destroy = new WrapperPlayServerEntityDestroy();
		destroy.setEntityId(this.armorStand.getEntityId());
		destroy.sendPacket(player);
	}
}
