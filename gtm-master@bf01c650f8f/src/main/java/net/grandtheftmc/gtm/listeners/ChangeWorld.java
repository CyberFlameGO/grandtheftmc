package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.gtm.GTM;
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
        if (Objects.equals(player.getWorld(), GTM.getWarpManager().getSpawn().getLocation().getWorld())) {
            player.setHealth(player.getMaxHealth());
            player.setFireTicks(0);

            if (!Core.getSettings().isSister())
                GTM.getDrugManager().getEffectManager().cancelEffects(player);
        } else {
            player.removePotionEffect(PotionEffectType.SPEED);
        }
    }
}
