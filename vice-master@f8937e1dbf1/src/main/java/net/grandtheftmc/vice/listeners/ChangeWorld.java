package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class ChangeWorld implements Listener {

    @EventHandler
    public void onSwitch(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if (Objects.equals(player.getWorld(), Vice.getWorldManager().getWarpManager().getSpawn().getLocation().getWorld())) {
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);
            Vice.getDrugManager().getEffectManager().cancelEffects(player);
        } else {
            player.removePotionEffect(PotionEffectType.SPEED);
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player)) continue;
            online.showPlayer(player);
        }
    }
}
