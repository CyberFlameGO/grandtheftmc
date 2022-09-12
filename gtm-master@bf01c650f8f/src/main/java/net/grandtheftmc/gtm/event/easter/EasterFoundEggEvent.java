package net.grandtheftmc.gtm.event.easter;

import net.grandtheftmc.core.events.CoreEvent;
import org.bukkit.entity.Player;

public final class EasterFoundEggEvent extends CoreEvent {

	private final Player player;
	private final EasterEgg easterEgg;
	private final boolean foundAll;

	public EasterFoundEggEvent(Player player, EasterEgg easterEgg, boolean foundAll) {
		super(false);
		this.player = player;
		this.easterEgg = easterEgg;
		this.foundAll = foundAll;
	}

	public Player getPlayer() {
		return player;
	}

	public EasterEgg getEasterEgg() {
		return easterEgg;
	}

	public boolean isFoundAll() {
		return foundAll;
	}
}
