package net.grandtheftmc.Creative;

import org.bukkit.entity.Player;

public class CreativeUtils {

	public static void spawnPlayer(final Player p) {
		p.teleport(Creative.getSpawn());
	}

}
