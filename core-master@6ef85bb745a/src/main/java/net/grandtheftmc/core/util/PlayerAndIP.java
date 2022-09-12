package net.grandtheftmc.core.util;

import org.bukkit.entity.Player;

public final class PlayerAndIP {
	private final Player player;
	private final String ip;

	private final CoreLocation coreLocation;

	public PlayerAndIP(Player player, String ip, CoreLocation coreLocation) {
		this.player = player;
		this.ip = ip;
		this.coreLocation = coreLocation;
	}

	public Player getPlayer() {
		return player;
	}

	public String getIp() {
		return ip;
	}

	public CoreLocation getCoreLocation() {
		return coreLocation;
	}
}
