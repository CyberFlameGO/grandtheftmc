package net.grandtheftmc.Creative.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;

public class BlockPlace implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player player = event.getPlayer();
		User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
		if (!user.isStaff() && (block.getType().equals(Material.DRAGON_EGG) || block.getType().equals(Material.BARRIER) | block.getType().equals(Material.NETHER_PORTAL)))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockChange(BlockFromToEvent event) {
        if (event.getToBlock().getType().equals(Material.NETHER_PORTAL)) {
            event.setCancelled(true);
        }
    }
}
