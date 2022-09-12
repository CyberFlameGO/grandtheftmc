package net.grandtheftmc.hub.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class Drop implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDrop(PlayerDropItemEvent e) {
        if (e.isCancelled()) e.setCancelled(false);
        Player p = e.getPlayer();
		ItemStack item = e.getItemDrop().getItemStack().clone();
		item.setAmount(p.getInventory().getItemInMainHand().getAmount() + item.getAmount());
		e.getItemDrop().remove();
		p.getInventory().setItem(p.getInventory().getHeldItemSlot(), item);
		p.updateInventory();
	}
}
