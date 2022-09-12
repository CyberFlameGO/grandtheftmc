package net.grandtheftmc.hub.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Move implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getFrom() == event.getTo()) return;
        Player player = event.getPlayer();
        if (player.isGliding() && player.isSneaking()) {
            double pitch = -event.getTo().getPitch();
            if (pitch < 10 || pitch > 90) return;
            Vector vector = player.getLocation().getDirection();
            player.setVelocity(vector.multiply(1.6));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 2.0F);
            player.getInventory().getChestplate().setDurability((short) 0);
        }
    }
}
