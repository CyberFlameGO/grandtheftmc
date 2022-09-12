package net.grandtheftmc.gtm.drugs;

import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.UUID;

public class EffectManager {

    private final HashMap<UUID, Long> cancelEffects = new HashMap<>();

    /**
     * @param player the player who will be getting the effect
     * @param effect the effect that may be addded / extended
     */
    public void addEffect(Player player, PotionEffect effect) {
        Bukkit.getScheduler().runTask(GTM.getInstance(), () -> {
            player.addPotionEffect(effect);
        });
    }

    /**
     * @param player   the player who is being checked
     * @param origTime the time that the player originally used the drug*
     * @return true if the player is still able to have the drug effects
     */
    public boolean canRecieveOngoingEffect(Player player, long origTime) {
        return player.isOnline() && player.isValid() && cancelEffects.getOrDefault(player.getUniqueId(), (long) 0) <= origTime;
    }


    /**
     * @param player the player whose effects will be cancelled
     */
    public void cancelEffects(Player player) {
        Bukkit.getScheduler().runTask(GTM.getInstance(), () -> {
            if (!cancelEffects.containsKey(player.getUniqueId())) {
                cancelEffects.put(player.getUniqueId(), System.currentTimeMillis());
            }
            for (PotionEffect p : player.getActivePotionEffects()) {
                player.removePotionEffect(p.getType());
            }
        });
    }
}
